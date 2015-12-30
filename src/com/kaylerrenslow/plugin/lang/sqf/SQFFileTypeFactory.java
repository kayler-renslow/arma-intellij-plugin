package com.kaylerrenslow.plugin.lang.sqf;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer){
        consumer.consume(SQFFileType.INSTANCE, SQFStatic.FILE_EXTENSION);
    }
}
