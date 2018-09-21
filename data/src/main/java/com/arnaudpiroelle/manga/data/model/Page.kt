package com.arnaudpiroelle.manga.data.model

import androidx.room.*

@Entity(tableName = "pages",
        indices = [
            Index("chapterId")
        ],
        foreignKeys = [
            ForeignKey(entity = Chapter::class,
                    parentColumns = ["id"],
                    childColumns = ["chapterId"],
                    onDelete = ForeignKey.CASCADE)])
data class Page(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "chapterId") val chapterId: Long,
        @ColumnInfo(name = "url") val url: String,
        @ColumnInfo(name = "postProcess") val postProcess: PostProcess) {

    enum class PostProcess {
        NONE,
        MOSAIC
    }
}