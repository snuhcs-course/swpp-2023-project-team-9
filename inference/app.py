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
    image = Image.open(BytesIO(base64.b64decode(param['file'])))
    image.save(f'{cur_path}/static/input/{ts}.png', "PNG")
    print(ts)
    # make subprocess in docker container have to option that "--cap-add=SYS_PRACTICE"
    # it processes video in static/output/{ts}/video.gif
    # if subprocess makes error in docker -> then just run image_to_animation function
    # p = subprocess.run(["python3.8", "../animated_drawing/AnimatedDrawings-main/examples/image_to_animation.py",
    #                     f'static/input/{ts}.png', f'static/output/{ts}'])

    remove_file_list.append(f'{cur_path}/static/input/{ts}.png')
    s3_client = S3_CLIENT

    motion_list = ["dab", "jumping", "wave_hello"] # , "jesse_dance"]

    print("here")
    for motion in motion_list:
        output_path = f'{cur_path}/static/output/{motion}/{ts}'
        os.makedirs(name=output_path, exist_ok=True)
        remove_file_list.append(upload_to_s3(s3_client, motion, ts, cur_path))
        remove_folder_list.append(output_path)
                 
    image_url = "https://little-studio.s3.amazonaws.com"

    for motion in motion_list:
        response_dict[motion] = f"{image_url}/drawings/{ts}/{motion}.gif"

    for remove in remove_file_list:
        os.remove(remove)
    for remove in remove_folder_list:
        clear_directory(remove)
        os.rmdir(remove)

    print("last")
    return jsonify(response_dict)


def upload_to_s3(s3_client, motion: str, ts: str, cur_path: str):
    image_to_animation(f'{cur_path}/static/input/{ts}.png', f'{cur_path}/static/output/{motion}/{ts}'
                       , f'{cur_path}/config/motion/{motion}.yaml'
                       , f'{cur_path}/config/retarget/fair1_ppf.yaml')
    s3_url = f"drawings/{ts}/{motion}.gif"
    ai_generated_image = f'{cur_path}/static/output/{motion}/{ts}/video.gif'
    with open(ai_generated_image, mode='rb') as gif:
       s3_client.upload_fileobj(gif, 'little-studio', s3_url)

    print(s3_url)
    return ai_generated_image

def clear_directory(directory):
    for item in os.listdir(directory):
        item_path = os.path.join(directory, item)
        if os.path.isfile(item_path):
            os.remove(item_path)
        elif os.path.isdir(item_path):
            clear_directory(item_path)
            os.rmdir(item_path)

S3_CLIENT = boto3.client(
    's3',
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY
)
