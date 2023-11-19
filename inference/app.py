import base64
import os
import boto3
import time

from examples.image_to_animation import image_to_animation
from config import AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY
from PIL import Image
from io import BytesIO
from flask import *

app = Flask(__name__)


@app.route('/', methods=['POST'])
def index():

    cur_path = os.getcwd()
    ts = str(time.time())
    remove_file_list = []
    remove_folder_list = []
    response_dict = {}
    param = request.get_json()
    image = Image.open(BytesIO(base64.b64decode(param["file"])))
    image.save(f'{cur_path}/static/input/{ts}.png', "PNG")

    # make subprocess in docker container have to option that "--cap-add=SYS_PRACTICE"
    # it processes video in static/output/{ts}/video.gif
    # if subprocess makes error in docker -> then just run image_to_animation function
    # p = subprocess.run(["python3.8", "../animated_drawing/AnimatedDrawings-main/examples/image_to_animation.py",
    #                     f'static/input/{ts}.png', f'static/output/{ts}'])

    remove_file_list.append(f'{cur_path}/static/input/{ts}.png')
    s3_client = S3_CLIENT

    motion_list = ["dab", "jumping", "wave_hello", "jesse_dance"]

    for motion in motion_list:
        output_path = f'{cur_path}/static/output/{motion}/{ts}'
        os.makedirs(name=output_path, exist_ok=True)
        remove_file_list.append(upload_to_s3(s3_client, motion, ts, cur_path))
        remove_folder_list.append(output_path)

    image_url = "https://little-studio.s3.amazonaws.com"

    for motion in motion_list:
        response_dict[motion] = f"{image_url}/drawings/{motion}/{ts}"

    for remove in remove_file_list:
        os.remove(remove)
    for remove in remove_folder_list:
        os.rmdir(remove)

    return make_response(jsonify(response_dict), 200)


def upload_to_s3(s3_client, motion: str, ts: str, cur_path: str):
    image_to_animation(f'{cur_path}/static/input/{ts}.png', f'{cur_path}/static/output/{motion}/{ts}'
                       , f'{cur_path}/config/motion/{motion}.yaml'
                       , f'{cur_path}/config/retarget/fair1_ppf.yaml')
    s3_url = f"drawings/{motion}/{ts}"
    ai_generated_image = f'{cur_path}/static/output/{motion}/{ts}/video.gif'
    gif = open(ai_generated_image, mode='r')
    s3_client.upload_fileobj(gif.read(), 'little-studio', s3_url)
    return ai_generated_image


S3_CLIENT = boto3.client(
    's3',
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY
)
