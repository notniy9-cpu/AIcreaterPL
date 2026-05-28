package org.Main.aiCreatorPL.ai;

import java.util.concurrent.CompletableFuture;

public interface AIProvider {
    CompletableFuture<String> chat(String message);
    CompletableFuture<String> generateStructure(String description);
}