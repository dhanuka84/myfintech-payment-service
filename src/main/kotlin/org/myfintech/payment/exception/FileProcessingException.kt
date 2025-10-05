package org.myfintech.payment.exception

import java.util.*

/**
 * Exception thrown when file processing fails.
 * Enhanced to include file-specific information.
 */
class FileProcessingException : RuntimeException {
    val fileName: String?
    val fileType: String?
    val fileSize: Long?

    constructor(message: String?) : super(message) {
        this.fileName = null
        this.fileType = null
        this.fileSize = null
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        this.fileName = null
        this.fileType = null
        this.fileSize = null
    }

    constructor(message: String?, fileName: String?) : super(message) {
        this.fileName = fileName
        this.fileType = extractFileType(fileName)
        this.fileSize = null
    }

    constructor(message: String?, fileName: String?, fileSize: Long?, cause: Throwable?) : super(message, cause) {
        this.fileName = fileName
        this.fileType = extractFileType(fileName)
        this.fileSize = fileSize
    }

    private fun extractFileType(fileName: String?): String {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown"
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).lowercase(Locale.getDefault())
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}