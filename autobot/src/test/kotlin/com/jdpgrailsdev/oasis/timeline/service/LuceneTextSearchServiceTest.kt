/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jdpgrailsdev.oasis.timeline.service

import com.jdpgrailsdev.oasis.timeline.data.TimelineData
import com.jdpgrailsdev.oasis.timeline.data.TimelineDataLoader
import io.mockk.every
import io.mockk.mockk
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LuceneTextSearchServiceTest {
  private lateinit var analyzer: Analyzer
  private lateinit var directory: Directory
  private lateinit var service: TextSearchService
  private lateinit var timelineDataLoader: TimelineDataLoader

  @BeforeEach
  fun setup() {
    directory = ByteBuffersDirectory()
    analyzer = StandardAnalyzer()
    timelineDataLoader = mockk()

    service =
      LuceneTextSearchService(
        analyzer = analyzer,
        directory = directory,
        timelineDataLoader = timelineDataLoader,
      )
  }

  @AfterEach
  fun cleanup() {
    directory.close()
  }

  @Test
  fun testIndexPopulation() {
    val data =
      listOf(
        mockk<TimelineData> {
          every { description } returns "text1"
          every { title } returns "Text 1"
        },
        mockk<TimelineData> {
          every { description } returns "text2"
          every { title } returns "Text 2"
        },
      )

    every { timelineDataLoader.getTimelineData() } returns data

    service.populateIndex()

    DirectoryReader.open(directory).use { reader -> assertEquals(data.size, reader.numDocs()) }
  }

  @Test
  fun testSearch() {
    val minimumScore = 1.0f
    val timelineData = generateTimelineData()

    every { timelineDataLoader.getTimelineData() } returns timelineData

    service.populateIndex()

    val result1 = service.search("Ernie paper boy loser", minimumScore)
    assertEquals(1, result1.size)
    assertEquals(timelineData.first().description, result1.first().timelineData.description)

    val result2 = service.search("Ernie", 0.3f)
    assertEquals(2, result2.size)
    assertEquals(timelineData.get(1).description, result2.first().timelineData.description)
  }

  @Test
  fun testSearchHighMinimumScore() {
    val minimumScore = 100.0f
    val timelineData = generateTimelineData().subList(0, 1)

    every { timelineDataLoader.getTimelineData() } returns timelineData

    service.populateIndex()

    val result = service.search("Ernie paper boy loser", minimumScore)
    assertEquals(0, result.size)
  }

  @Test
  fun testPaginatedResults() {
    val minimumScore = 0.1f
    val timelineData = generateTimelineData()

    every { timelineDataLoader.getTimelineData() } returns timelineData

    service.populateIndex()

    for (page in 0..4) {
      val result = service.search("Ernie paper boy loser", minimumScore, limit = 1, offset = page)
      assertEquals(if (page < timelineData.size) 1 else 0, result.size)
    }
  }

  private fun generateTimelineData() =
    listOf(
      mockk<TimelineData> {
        every { description } returns
          "On this day, the paper boy is working before he goes, lying to the teacher who knows he knows.  He didn't and he should've brought his lines in yesterday."
        every { title } returns "Verse 1"
      },
      mockk<TimelineData> {
        every { description } returns
          "On this day, Ernie bangs the sound and the day begins.  The letterbox is open and your cash falls in.  I'll meet you at the office just before the staff clock in."
        every { title } returns "Verse 2"
      },
      mockk<TimelineData> {
        every { description } returns
          "On this day, the game is kicking off in around the park.  It's twenty five a side and before it's dark.  There's gonna be a loser and you know the next goal wins."
        every { title } returns "Verse 3"
      },
      mockk<TimelineData> {
        every { description } returns
          "On this day, cab it to the front as it's called a draw.  Everybody's knocking at yours once more.  Ernie bangs the sound and no one's spoken since half past four...la, la, la, laaa, laa."
        every { title } returns "Verse 4"
      },
    )
}
