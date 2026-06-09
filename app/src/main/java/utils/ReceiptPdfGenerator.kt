package com.example.kariainventoryapp.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

class ReceiptPdfGenerator {

    fun createReceipt(
        context: Context,
        receiptId: String,
        productName: String,
        quantity: Int,
        unitPrice: Double,
        total: Double,
        branchName: String,
        date: String
    ): File? {

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        var y = 30

        paint.textSize = 14f
        canvas.drawText("INVENTORY RECEIPT", 80f, y.toFloat(), paint)

        y += 30
        paint.textSize = 12f

        canvas.drawText("Receipt ID: $receiptId", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Branch: $branchName", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Product: $productName", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Quantity: $quantity", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Unit Price: $unitPrice", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("TOTAL: $total", 10f, y.toFloat(), paint)
        y += 20

        canvas.drawText("Date: $date", 10f, y.toFloat(), paint)

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "receipt_$receiptId.pdf")

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }
}