import os
import tensorflow as tf
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input, decode_predictions
from tensorflow.keras.preprocessing import image
import numpy as np
import sys

def main(image_path = sys.argv[1]):
    try:
        # Validate and expand the path
        image_path = os.path.expanduser(image_path)
        if not os.path.isfile(image_path):
            print(f"File not found: {image_path}")
            return None

        # Load the pre-trained MobileNetV2 model
        model = MobileNetV2(weights='imagenet')
        # Load and preprocess the image
        img = image.load_img(image_path, target_size=(224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array = preprocess_input(img_array)
        # Predict the class
        predictions = model.predict(img_array)
        decoded_predictions = decode_predictions(predictions, top=1)[0]

        return f"{decoded_predictions[0][1]} {decoded_predictions[0][2]* 100:.2f}"   # Return the top label
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    result = main()
    print(result, flush=True)
    sys.exit(0)