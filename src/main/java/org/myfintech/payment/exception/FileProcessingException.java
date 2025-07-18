package org.myfintech.payment.exception;

/**
 * Exception thrown when file processing fails.
 * Enhanced to include file-specific information.
 */
public class FileProcessingException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
	private final String fileName;
    private final String fileType;
    private final Long fileSize;
    
    public FileProcessingException(String message) {
        super(message);
        this.fileName = null;
        this.fileType = null;
        this.fileSize = null;
    }
    
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.fileName = null;
        this.fileType = null;
        this.fileSize = null;
    }
    
    public FileProcessingException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
        this.fileType = extractFileType(fileName);
        this.fileSize = null;
    }
    
    public FileProcessingException(String message, String fileName, Long fileSize, Throwable cause) {
        super(message, cause);
        this.fileName = fileName;
        this.fileType = extractFileType(fileName);
        this.fileSize = fileSize;
    }
    
    private String extractFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
}