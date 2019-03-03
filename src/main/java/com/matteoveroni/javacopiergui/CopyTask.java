package com.matteoveroni.javacopiergui;

import com.matteoveroni.javacopier.CopyListener;
import com.matteoveroni.javacopier.CopyStatusReport;
import com.matteoveroni.javacopier.JavaCopier;
import javafx.concurrent.Task;

import java.nio.file.CopyOption;
import java.nio.file.Path;

/**
 * @author Matteo Veroni
 */
public class CopyTask extends Task<CopyStatusReport> {

    private static final int MAX_PERCENTAGE = 0;
    private static final int MIN_PERCENTAGE = 0;

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
        updateProgress(MIN_PERCENTAGE, MAX_PERCENTAGE);
        updateMessage("");

        final CopyListener copyListener = new CopyListener() {
            @Override
            public void onCopyProgress(CopyStatusReport copyStatusReport) {
                double progress = copyStatusReport.getCopyPercentage() / 100;
                updateProgress(progress, MAX_PERCENTAGE);
                updateMessage(copyStatusReport.getCopyHistory().getLastCopyHistoryEventMessage());
            }

            @Override
            public void onCopyComplete(CopyStatusReport copyStatusReport) {
                updateMessage("Copy finished");
                updateProgress(MAX_PERCENTAGE, MAX_PERCENTAGE);
            }
        };
        return JavaCopier.copy(src, dest, copyListener, copyOptions);
    }
}
