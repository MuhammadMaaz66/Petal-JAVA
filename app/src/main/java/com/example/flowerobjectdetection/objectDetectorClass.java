package com.example.flowerobjectdetection;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class objectDetectorClass {
    // Interpreter for loading model and predicting
    private Interpreter interpreter;
    // Store all labels in a list
    private List<String> labelList;
    private int INPUT_SIZE;
    private int PIXEL_SIZE = 3; // for RGB
    private int IMAGE_MEAN = 0;
    private float IMAGE_STD = 255.0f;
    private int height = 0;
    private int width = 0;
    private Context context;

    public objectDetectorClass(Context context, AssetManager assetManager, String modelPath, String labelPath, int inputSize) throws IOException {
        this.context = context;
        INPUT_SIZE = inputSize;
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4); // Set it according to your phone
        interpreter = new Interpreter(loadModelFile(assetManager, modelPath), options);
        labelList = loadLabelList(assetManager, labelPath);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Mat recognizeImage(Mat mat_image) {
        Mat rotated_mat_image = new Mat();
        Mat a = mat_image.t();
        Core.flip(a, rotated_mat_image, 1);
        a.release();

        Bitmap bitmap = Bitmap.createBitmap(rotated_mat_image.cols(), rotated_mat_image.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rotated_mat_image, bitmap);

        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);

        Object[] input = new Object[1];
        input[0] = byteBuffer;

        Map<Integer, Object> output_map = new TreeMap<>();
        float[][][] boxes = new float[1][10][4];
        float[][] scores = new float[1][10];
        float[][] classes = new float[1][10];

        output_map.put(1, boxes);
        output_map.put(3, classes);
        output_map.put(0, scores);

        interpreter.runForMultipleInputsOutputs(input, output_map);

        float[][][] detected_boxes = (float[][][]) output_map.get(1);
        float[][] detected_scores = (float[][]) output_map.get(0);
        float[][] detected_classes = (float[][]) output_map.get(3);

   /*     for (int i = 0; i < 10; i++) {
            assert detected_classes != null;
            float class_value = detected_classes[0][i];
            float score_value = detected_scores[0][i];

            if (score_value > 0.5) {
                float[] box = detected_boxes[0][i];
                float top = box[0] * height;
                float left = box[1] * width;
                float bottom = box[2] * height;
                float right = box[3] * width;

                Imgproc.rectangle(rotated_mat_image, new Point(left, top), new Point(right, bottom), new Scalar(0, 255, 0, 255), 2);
                String detectedLabel = labelList.get((int) class_value);
                Imgproc.putText(rotated_mat_image, detectedLabel + " " + score_value, new Point(left, top), 3, 1, new Scalar(255, 0, 0, 255), 2);

                launchActivityForDetectedLabel(detectedLabel);
            }
        }
*/
        // Ensure detected_boxes is not null and has elements
        if (detected_boxes != null && detected_boxes.length > 0 && detected_boxes[0] != null) {
            // Loop through each detected box
            for (int i = 0; i < detected_boxes[0].length; i++) {
                float class_value = detected_classes[0][i];
                float score_value = detected_scores[0][i];

                if (score_value > 0.5) {
                    // Ensure i does not exceed the length of detected_boxes[0]
                    if (i < detected_boxes[0].length) {
                        float[] box = detected_boxes[0][i];
                        // Extract coordinates from the box array
                        float top = box[0] * height;
                        float left = box[1] * width;
                        float bottom = box[2] * height;
                        float right = box[3] * width;

                        // Draw rectangle and label on the image
                        Imgproc.rectangle(rotated_mat_image, new Point(left, top), new Point(right, bottom), new Scalar(0, 255, 0, 255), 2);
                        String detectedLabel = labelList.get((int) class_value);
                        Imgproc.putText(rotated_mat_image, detectedLabel + " " + score_value, new Point(left, top), 3, 1, new Scalar(255, 0, 0, 255), 2);

                        // Launch activity for the detected label
                        launchActivityForDetectedLabel(detectedLabel);
                    }
                }
            }
        }

        Mat b = rotated_mat_image.t();
        Core.flip(b, mat_image, 0);
        b.release();
        return mat_image;
    }

    private void launchActivityForDetectedLabel(String label) {



// Assuming label is coming from some detection method, add logging there
        Log.d("PlantDetection", "Detected label: " + label);

        // Ensure context is valid
        if (context == null) {
            Log.e("PlantDetection", "Context is null!");
            return;
        } else {
            Log.d("PlantDetection", "Context is valid");
        }

        // Handle different plant labels with if-else statements
        if ("Sunflower".equals(label)) {
            Log.d("PlantDetection", "Detected label: Sunflower");
            Intent sunflowerIntent = new Intent(context, SunflowerInfo.class);
            context.startActivity(sunflowerIntent);

        }
        if ("Eggplant".equals(label)) {
            Log.d("PlantDetection", "Detected label: Eggplant");
            Intent eggplantIntent = new Intent(context, EggplantInfo.class);
            context.startActivity(eggplantIntent);
        }
        if ("Bougainvillea".equals(label)) {
            Log.d("PlantDetection", "Detected label: Bougainvillea");
            Intent bougainvilleaIntent = new Intent(context, BougainvilleaInfo.class);
            context.startActivity(bougainvilleaIntent);
        }
        if ("ClimbingRose".equals(label)) {
            Log.d("PlantDetection", "Detected label: Rose");
            Intent climbingroseIntent = new Intent(context, ClimbingroseInfo.class);
            context.startActivity(climbingroseIntent);
        } else {
            Log.d("PlantDetection", "No match for label: " + label);
            // Optionally handle the default case
            // Intent defaultIntent = new Intent(context, DefaultActivity.class);
            // context.startActivity(defaultIntent);
        }
    }

    //chage the condition to if else, we need to hardcoded of this, the case statement are not accurate when itcomes

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;
        int quant = 1;
        int size_images = INPUT_SIZE;

        if (quant == 0) {
            byteBuffer = ByteBuffer.allocateDirect(1 * size_images * size_images * 3);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * 1 * size_images * size_images * 3);
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[size_images * size_images];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        for (int i = 0; i < size_images; ++i) {
            for (int j = 0; j < size_images; ++j) {
                final int val = intValues[pixel++];
                if (quant == 0) {
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                } else {
                    byteBuffer.putFloat((((val >> 16) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat((((val >> 8) & 0xFF)) / 255.0f);
                    byteBuffer.putFloat(((val & 0xFF)) / 255.0f);
                }
            }
        }
        return byteBuffer;
    }
}
