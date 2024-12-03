package com.example.imgclassapp;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.model.MobileNetV2;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MobileNetClassifier {
    private ComputationGraph model;
    private static final int IMAGE_SIZE = 224; // Standard MobileNet input size
    private static final String[] IMAGENET_CLASSES = {
            // This is a placeholder. You'd replace with actual ImageNet class names
            "dog", "cat", "car", "airplane" // Example classes
    };

    public MobileNetClassifier() {
        try {
            // Load pre-trained MobileNetV2 model
            model = MobileNetV2.builder()
                    .pretrainedType(PretrainedType.IMAGENET)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String classifyImage(File imageFile) {
        try {
            // Read the image
            BufferedImage bufferedImage = ImageIO.read(imageFile);

            // Preprocess the image (resize, normalize)
            INDArray inputArray = preprocessImage(bufferedImage);

            // Perform classification
            INDArray output = model.outputSingle(inputArray);

            // Get the index of the highest probability
            int maxIndex = output.argMax(1).getInt(0);

            // Return the class name
            return IMAGENET_CLASSES[maxIndex];
        } catch (IOException e) {
            e.printStackTrace();
            return "Classification failed";
        }
    }

    private INDArray preprocessImage(BufferedImage image) {
        // Resize image to MobileNet input size
        BufferedImage resizedImage = new BufferedImage(
                IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_3BYTE_BGR);

        // Perform resizing and normalization steps
        // Note: You might want to use a more sophisticated resizing method
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
        g.dispose();

        // Convert to INDArray and normalize
        return imageToINDArray(resizedImage);
    }

    private INDArray imageToINDArray(BufferedImage image) {
        // Convert BufferedImage to INDArray with normalization
        int width = image.getWidth();
        int height = image.getHeight();

        float[] pixels = new float[width * height * 3];
        int idx = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                pixels[idx++] = ((rgb >> 16) & 0xFF) / 255.0f;  // Red
                pixels[idx++] = ((rgb >> 8) & 0xFF) / 255.0f;   // Green
                pixels[idx++] = (rgb & 0xFF) / 255.0f;          // Blue
            }
        }

        return Nd4j.create(pixels)
                .reshape(1, 3, height, width)  // Reshape to [batch, channels, height, width]
                .div(255.0);  // Normalize to [0,1]
    }
}

// Example JavaFX integration
public class ImageClassificationController {
    @FXML
    private Label resultLabel;

    @FXML
    private void classifyImage() {
        // File chooser logic to select image
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            MobileNetClassifier classifier = new MobileNetClassifier();
            String result = classifier.classifyImage(selectedFile);
            resultLabel.setText("Classified as: " + result);
        }
    }
}