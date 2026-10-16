package com.mooddiary.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Write-to-temp-then-rename, so a crash never leaves a half-written file. */
final class AtomicFiles {

    private AtomicFiles() { }

    static void write(Path target, String content) throws IOException {
        Path dir = target.toAbsolutePath().getParent();
        Path temp = Files.createTempFile(dir, ".tmp-", ".part");
        try {
            Files.writeString(temp, content, StandardCharsets.UTF_8);
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
