package com.example.matlabplugin

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile

class MatlabMatrixErrorFilter : HighlightInfoFilter {

    override fun accept(highlightInfo: HighlightInfo, file: PsiFile?): Boolean {
        // Sadece Java dosyalarında çalış
        if (file !is PsiJavaFile) return true

        val project: Project = file.project
        val document: Document =
            PsiDocumentManager.getInstance(project).getDocument(file) ?: return true

        val startOffset = highlightInfo.startOffset
        if (startOffset < 0 || startOffset >= document.textLength) return true

        // Hatanın bulunduğu satırı al
        val lineNumber = document.getLineNumber(startOffset)
        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)
        val lineText = document.getText(TextRange(lineStart, lineEnd))

        // Whitespace’i normalize et
        val normalized = lineText.replace("\\s+".toRegex(), " ").trim()

        // A = [1 2; 3 4]; gibi mi?
        val isMatrixDsl = normalized.matches(
            Regex("[A-Za-z_]\\w*\\s*=\\s*\\[.+];")
        )

        // true => highlight kalsın, false => highlight gizlensin
        return !isMatrixDsl
    }
}
