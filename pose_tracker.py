import sys
import time
import math

import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision


MODEL_PATH = "pose_landmarker_lite.task"

NOSE = 0
LEFT_SHOULDER = 11
RIGHT_SHOULDER = 12
LEFT_ELBOW = 13
RIGHT_ELBOW = 14
LEFT_WRIST = 15
RIGHT_WRIST = 16

DOWN_ANGLE = 90
UP_ANGLE = 160


def angle_at(a, b, c):
    ba_x, ba_y = a[0] - b[0], a[1] - b[1]
    bc_x, bc_y = c[0] - b[0], c[1] - b[1]
    radians = math.atan2(bc_y, bc_x) - math.atan2(ba_y, ba_x)
    degrees = abs(math.degrees(radians))
    if degrees > 180:
        degrees = 360 - degrees
    return degrees


def pixel(landmarks, i, w, h):
    lm = landmarks[i]
    return (lm.x * w, lm.y * h)


def all_visible(landmarks, ids, threshold=0.5):
    return all(landmarks[i].visibility > threshold for i in ids)


def line(frame, p1, p2):
    cv2.line(frame, (int(p1[0]), int(p1[1])), (int(p2[0]), int(p2[1])),
             (255, 255, 255), 2)


def detect_pushup(landmarks, frame, w, h, stage):
    needed = [LEFT_SHOULDER, LEFT_ELBOW, LEFT_WRIST,
              RIGHT_SHOULDER, RIGHT_ELBOW, RIGHT_WRIST]
    if not all_visible(landmarks, needed):
        return stage, 0, "Get both arms in frame"

    l_sh = pixel(landmarks, LEFT_SHOULDER, w, h)
    l_el = pixel(landmarks, LEFT_ELBOW, w, h)
    l_wr = pixel(landmarks, LEFT_WRIST, w, h)
    r_sh = pixel(landmarks, RIGHT_SHOULDER, w, h)
    r_el = pixel(landmarks, RIGHT_ELBOW, w, h)
    r_wr = pixel(landmarks, RIGHT_WRIST, w, h)

    hands_down = l_wr[1] > l_sh[1] and r_wr[1] > r_sh[1]
    if not hands_down:
        return "up", 0, "Hands on the floor to start"

    angle = (angle_at(l_sh, l_el, l_wr) + angle_at(r_sh, r_el, r_wr)) / 2
    line(frame, l_sh, l_el); line(frame, l_el, l_wr)
    line(frame, r_sh, r_el); line(frame, r_el, r_wr)

    reps_to_add = 0
    if angle < DOWN_ANGLE:
        stage = "down"
    if angle > UP_ANGLE and stage == "down":
        stage = "up"
        reps_to_add = 1
    return stage, reps_to_add, f"{stage}  ({int(angle)} deg)"


def main():
    exercise = "pushups"
    detect = detect_pushup
    stage = "up"

    base_options = python.BaseOptions(model_asset_path=MODEL_PATH)
    options = vision.PoseLandmarkerOptions(
        base_options=base_options,
        running_mode=vision.RunningMode.VIDEO,
        num_poses=1)
    landmarker = vision.PoseLandmarker.create_from_options(options)

    camera = cv2.VideoCapture(0)
    if not camera.isOpened():
        print("ERROR could not open the webcam", file=sys.stderr)
        sys.exit(1)

    reps = 0
    start_time = time.time()
    timestamp_ms = 0

    while True:
        ok, frame = camera.read()
        if not ok:
            break

        frame = cv2.flip(frame, 1)
        h, w = frame.shape[:2]

        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        mp_image = mp.Image(image_format=mp.ImageFormat.SRGB, data=rgb)
        timestamp_ms += 33
        result = landmarker.detect_for_video(mp_image, timestamp_ms)

        status = "No person detected"
        if result.pose_landmarks:
            landmarks = result.pose_landmarks[0]

            for lm in landmarks:
                cv2.circle(frame, (int(lm.x * w), int(lm.y * h)),
                           4, (0, 200, 255), -1)

            stage, reps_to_add, status = detect(landmarks, frame, w, h, stage)
            reps += reps_to_add

        cv2.rectangle(frame, (0, 0), (260, 90), (0, 0, 0), -1)
        cv2.putText(frame, exercise.upper(), (10, 25),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
        cv2.putText(frame, f"Reps: {reps}", (10, 70),
                    cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 255, 0), 3)
        cv2.putText(frame, status, (270, 25),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 255), 2)
        cv2.putText(frame, "Press 'q' to finish", (10, h - 15),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 0), 2)

        cv2.imshow("Fitness Tracker - Live Pose", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    camera.release()
    cv2.destroyAllWindows()
    landmarker.close()

    seconds = int(time.time() - start_time)
    print(f"{exercise.upper()} {reps} {seconds}")


if __name__ == "__main__":
    main()
