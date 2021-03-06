/*
 * Copyright (c) 2014, Dennis M. Sosnoski.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.sosnoski.concur.article2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.sosnoski.concur.article1.DistancePair;
import com.sosnoski.concur.article1.TimingTestBase;

/**
 * Run timed test of finding best matches for misspelled words.
 */
public class CompletableFutureDistance0 extends TimingTestBase {
    private final List<ChunkDistanceChecker> chunkCheckers;

    private final int blockSize;

    public CompletableFutureDistance0(String[] words, int block) {
        blockSize = block;
        chunkCheckers = ChunkDistanceChecker.buildCheckers(words, block);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public int blockSize() {
        return blockSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sosnoski.concur1.TimedTest#bestMatch(java.lang.String)
     */
    @Override
    public DistancePair bestMatch(String target) {
        List<CompletableFuture<DistancePair>> futures = new ArrayList<>();
        for (ChunkDistanceChecker checker: chunkCheckers) {
            CompletableFuture<DistancePair> future = CompletableFuture.supplyAsync(() -> checker.bestDistance(target));
            futures.add(future);
        }
        DistancePair best = DistancePair.worstMatch();
        for (CompletableFuture<DistancePair> future: futures) {
            best = DistancePair.best(best, future.join());
        }
        return best;
    }
}