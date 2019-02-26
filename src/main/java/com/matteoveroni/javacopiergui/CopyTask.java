package com.matteoveroni.javacopiergui;

import com.matteoveroni.javacopier.CopyListener;
import com.matteoveroni.javacopier.CopyStatusReport;
import com.matteoveroni.javacopier.JavaCopier;
import javafx.concurrent.Task;

import java.nio.file.CopyOption;
import java.nio.file.Path;

public class CopyTask extends Task<CopyStatusReport> {

    private final Path src;
    private final Path dest;
    private final CopyOption[] copyOptions;

    public CopyTask(Path src, Path dest, CopyOption... copyOptions) {
        this.src = src;
        this.dest = dest;
        this.copyOptions = copyOptions;
    }

    @Override
    protected CopyStatusReport call() throws Exception {

        final CopyListener copyListener = new CopyListener() {
            @Override public void onCopyProgress(CopyStatusReport copyStatusReport) {
                double progress = copyStatusReport.getCopyPercentage() / 100;
                updateProgress(progress, 1);
                updateMessage(copyStatusReport.getCopyHistory().getLastCopyHistoryEventMessage());
            }

            @Override public void onCopyComplete(CopyStatusReport copyStatusReport) {
                updateMessage("Copy finished");
                updateProgress(1, 1);
            }
        };
        updateProgress(0, 1);
        updateMessage("");
        return JavaCopier.copy(src, dest, copyListener, copyOptions);
    }
}
