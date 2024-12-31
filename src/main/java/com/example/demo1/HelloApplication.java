package com.example.demo1;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloApplication extends Application {
    private RSAEncryptDecrypt rsa; // RSA encryption/decryption handler
    private TextField textField;
    private Button selectFileButton, processButton;
    private Label selectedFileLabel;
    private RadioButton textRadioButton, fileRadioButton;
    private RadioButton encryptRadioButton, decryptRadioButton;
    private ToggleGroup typeToggleGroup, operationToggleGroup;
    private File selectedFile;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RSA Encryption and Decryption");

        // Initialize RSA instance (use your own RSA key values)
        RSAKeyGenerator keyGen = new RSAKeyGenerator();
        rsa = new RSAEncryptDecrypt(keyGen.getN(), keyGen.getE(), keyGen.getD());

        // Create RadioButtons for selecting Text or File
        textRadioButton = new RadioButton("Text");
        fileRadioButton = new RadioButton("File");

        typeToggleGroup = new ToggleGroup();
        textRadioButton.setToggleGroup(typeToggleGroup);
        fileRadioButton.setToggleGroup(typeToggleGroup);

        // Create RadioButtons for selecting Encrypt or Decrypt
        encryptRadioButton = new RadioButton("Encrypt");
        decryptRadioButton = new RadioButton("Decrypt");

        operationToggleGroup = new ToggleGroup();
        encryptRadioButton.setToggleGroup(operationToggleGroup);
        decryptRadioButton.setToggleGroup(operationToggleGroup);

        // Create TextField for text input
        textField = new TextField();
        textField.setPromptText("Enter text to encrypt/decrypt");
        textField.setVisible(false);

        // Create File Selection button and label
        selectFileButton = new Button("Select File");
        selectFileButton.setVisible(false);

        selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setVisible(false);

        // Button to process the operation
        processButton = new Button("Process");
        processButton.setDisable(true);

        // Layout for file selection components
        HBox fileSelectionBox = new HBox(10, selectFileButton, selectedFileLabel);
        fileSelectionBox.setAlignment(Pos.CENTER_LEFT);



        // Layout for operation selection (Encrypt/Decrypt)
        HBox operationBox = new HBox(10, encryptRadioButton, decryptRadioButton);
        operationBox.setAlignment(Pos.CENTER_LEFT);

        // VBox to hold all input components
        VBox inputBox = new VBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setPadding(new Insets(20));
        inputBox.getChildren().addAll(
                textRadioButton, fileRadioButton,
                textField, fileSelectionBox,
                new Label("Select Operation:"), operationBox,
                processButton
        );

        // Event handler for RadioButtons to toggle visibility
        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == textRadioButton) {
                textField.setVisible(true);
                selectFileButton.setVisible(false);
                selectedFileLabel.setVisible(false);
            } else if (newValue == fileRadioButton) {
                textField.setVisible(false);
                selectFileButton.setVisible(true);
                selectedFileLabel.setVisible(true);
            }
            updateProcessButtonState();
        });

        // Event handler for FileChooser button
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg", "*.txt"));
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                selectedFileLabel.setText("Selected: " + selectedFile.getName());
            } else {
                selectedFileLabel.setText("No file selected");
            }
            updateProcessButtonState();
        });

        // Event handler for operation selection
        operationToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateProcessButtonState();
        });

        // Event handler for process button
        processButton.setOnAction(e -> {
            if (textRadioButton.isSelected()) {
                processTextOperation();
            } else if (fileRadioButton.isSelected()) {
                processFileOperation();
            }
        });

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(inputBox, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Enable the process button only when all required selections are made
    private void updateProcessButtonState() {
        boolean isTypeSelected = typeToggleGroup.getSelectedToggle() != null;
        boolean isOperationSelected = operationToggleGroup.getSelectedToggle() != null;
        boolean isInputValid = false;

        if (textRadioButton.isSelected()) {
            isInputValid = !textField.getText().isEmpty();
        } else if (fileRadioButton.isSelected()) {
            isInputValid = selectedFile != null;
        }

        processButton.setDisable(!(isTypeSelected && isOperationSelected && isInputValid));
    }

    // Text operation processing
    private void processTextOperation() {
        String operation = ((RadioButton) operationToggleGroup.getSelectedToggle()).getText();
        String inputText = textField.getText();
        String result;

        try {
            if (operation.equals("Encrypt")) {
                result = rsa.encryptText(inputText);
            } else {
                result = rsa.decryptText(inputText);
            }
            // Display the result in the text field
            textField.setText(result);
        } catch (Exception ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage());
        }
    }

    // File operation processing (for images)

    // File operation processing
    private void processFileOperation() {
        String operation = ((RadioButton) operationToggleGroup.getSelectedToggle()).getText();
        File outputFile = new File(selectedFile.getParent(),
                (operation.equals("Encrypt") ? "encrypted_" : "decrypted_") + selectedFile.getName());

        try {
            String fileExtension = getFileExtension(selectedFile);

            if (fileExtension.equals("txt")) {
                // Handle text file encryption/decryption
                if (operation.equals("Encrypt")) {
                    String textData = new String(Files.readAllBytes(selectedFile.toPath()));
                    String encryptedText = rsa.encryptText(textData);
                    Files.write(outputFile.toPath(), encryptedText.getBytes());
                } else {
                    String encryptedText = new String(Files.readAllBytes(selectedFile.toPath()));
                    String decryptedText = rsa.decryptText(encryptedText);
                    Files.write(outputFile.toPath(), decryptedText.getBytes());
                }
            } else if (isImageFile(fileExtension)) {
                // Handle image file encryption/decryption
                byte[] fileData = Files.readAllBytes(selectedFile.toPath());
                if (operation.equals("Encrypt")) {
                    byte[] encryptedImageData = rsa.encryptImage(fileData);
                    Files.write(outputFile.toPath(), encryptedImageData);
                } else {
                    byte[] encryptedImageData = Files.readAllBytes(selectedFile.toPath());
                    byte[] decryptedImageData = rsa.decryptImage(encryptedImageData);
                    Files.write(outputFile.toPath(), decryptedImageData);
                }
            } else {
                showAlert("Error", "Unsupported file type: " + fileExtension);
                return;
            }

            showAlert(operation + " Success", "File saved at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    // Helper method to get the file extension
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase();
    }

    // Helper method to check if the file is an image
    private boolean isImageFile(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
    }


    // Utility method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
