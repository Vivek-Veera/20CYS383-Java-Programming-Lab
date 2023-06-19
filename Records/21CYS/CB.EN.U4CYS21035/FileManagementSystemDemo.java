package com.amrita.jpl.cys21035.endsem;

import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

enum FileType {
    DOCUMENT, IMAGE, VIDEO
}
abstract class File {
    private String fileName;
    private long fileSize;

    public File(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public abstract void displayFileDetails();
}
interface FileManager {
    void addFile(FileType fileType, File file);
    void deleteFile(String fileName);
    void saveToFile();
    void loadFromFile();
    ArrayList<File> getFiles();
}
class Document extends File {
    private static String documentType;

    public Document(String fileName, long fileSize, String documentType) {
        super(fileName, fileSize);
        this.documentType = documentType;
    }

    public static String getDocumentType() {
        return documentType;
    }

    public void displayFileDetails() {
        System.out.println("Document Name: " + getFileName());
        System.out.println("Document Size: " + getFileSize());
        System.out.println("Document Type: " + getDocumentType());
    }
}

class Image extends File {
    private static String resolution;

    public Image(String fileName, long fileSize, String resolution) {
        super(fileName, fileSize);
        this.resolution = resolution;
    }

    public static String getResolution() {
        return resolution;
    }

    public void displayFileDetails() {
        System.out.println("Image Name: " + getFileName());
        System.out.println("Image Size: " + getFileSize());
        System.out.println("Resolution: " + getResolution());
    }
}

class Video extends File {
    private static String duration;

    public Video(String fileName, long fileSize, String duration) {
        super(fileName, fileSize);
        this.duration = duration;
    }

    public static String getDuration() {

        return duration;
    }

    public void displayFileDetails() {
        System.out.println("Video Name: " + getFileName());
        System.out.println("Video Size: " + getFileSize());
        System.out.println("Duration: " + getDuration());
    }
}

class FileManagerImpl implements FileManager {
    private ArrayList<File> files;

    public FileManagerImpl() {
        files = new ArrayList<>();
    }

    public void addFile(FileType fileType, File file) {
        files.add(file);
    }

    public void deleteFile(String fileName) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getFileName().equals(fileName)) {
                files.remove(i);
                break;
            }
        }
    }

    public void saveToFile() {
        try (FileOutputStream fos = new FileOutputStream("D:\\Java\\cbenu4cys21035_jpl_final\\fileDetails.txt");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(files);
            System.out.println("Saved to txt");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void loadFromFile() {
        try (FileInputStream fis = new FileInputStream("D:\\Java\\cbenu4cys21035_jpl_final\\fileDetails.txt");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            files = (ArrayList<File>) ois.readObject();
            System.out.println("Loaded from txt");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    public ArrayList<File> getFiles() {
        return files;
    }
}
class FileManagementSystemUI {
    private JFrame frame;
    private JTextField nameField, sizeField, typeField;
    private JComboBox<FileType> fileTypeComboBox;
    private DefaultTableModel tableModel;
    private FileManager fileManager;

    public FileManagementSystemUI() {
        frame = new JFrame("21CYS End Semester Assignment File Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 400);



        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel filePanel = new JPanel();
        filePanel.add(new JLabel("File Name:"));
        nameField = new JTextField(20);
        filePanel.add(nameField);
        filePanel.add(new JLabel("File Size:"));
        sizeField = new JTextField(10);
        filePanel.add(sizeField);

        filePanel.add(new JLabel("File Type:"));
        typeField = new JTextField(10);
        filePanel.add(typeField);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        tableModel = new DefaultTableModel(new Object[]{"File Name", "File Size", "File Type"},0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        fileTypeComboBox = new JComboBox<>(FileType.values());
        filePanel.add(fileTypeComboBox);

        JPanel specificPanel = new JPanel();
        specificPanel.setLayout(new BoxLayout(specificPanel, BoxLayout.X_AXIS));

        filePanel.add(specificPanel);
        inputPanel.add(filePanel);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add File");
        addButton.addActionListener(e -> addFile());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete File");
        deleteButton.addActionListener(e -> deleteFile(table));
        inputPanel.add(deleteButton);

        JButton loadButton = new JButton("Load from File");
        loadButton.addActionListener(e -> loadFromFile());
        inputPanel.add(loadButton);

        panel.add(inputPanel);

        frame.add(panel);
        frame.setVisible(true);

        fileManager = new FileManagerImpl();
    }

    private void addFile() {
        String name = nameField.getText();
        long size = Long.parseLong(sizeField.getText());
        FileType fileType = (FileType) fileTypeComboBox.getSelectedItem();

        switch (fileType) {
            case DOCUMENT:
                String documentType = Document.getDocumentType();
                Document document = new Document(name, size, documentType);
                fileManager.addFile(fileType, document);
                break;
            case IMAGE:
                String resolution = Image.getResolution();
                Image image = new Image(name, size, resolution);
                fileManager.addFile(fileType, image);
                break;
            case VIDEO:
                String duration = Video.getDuration();
                Video video = new Video(name, size, duration);
                fileManager.addFile(fileType, video);
                break;
        }

        clearFields();
        displayFiles();
    }


    private void deleteFile(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String fileName = (String) table.getValueAt(selectedRow, 0);
            fileManager.deleteFile(fileName);
            displayFiles();
        }
    }

    private void displayFiles() {
        ArrayList<File> files = fileManager.getFiles();
        tableModel.setRowCount(0);
        for (File file : files) {
            tableModel.addRow(new Object[]{file.getFileName(), file.getFileSize()});
        }
    }


    private void loadFromFile() {
        fileManager.loadFromFile();
        displayFiles();
        JOptionPane.showMessageDialog(frame, "File details loaded from file.", "Load", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        sizeField.setText("");
        ((JTextField) ((JPanel) ((JPanel) fileTypeComboBox.getParent())
                .getComponent(2)).getComponent(0)).setText("");
    }
}



public class FileManagementSystemDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileManagementSystemUI::new);
    }
}

