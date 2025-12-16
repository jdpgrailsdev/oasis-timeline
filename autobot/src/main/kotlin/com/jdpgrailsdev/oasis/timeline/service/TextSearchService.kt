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
import com.jdpgrailsdev.oasis.timeline.search.TimelineDataSearchResult
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import kotlin.text.toInt

private val logger = KotlinLogging.logger {}

internal const val DESCRIPTION_SEARCH_FIELD_NAME = "description"
internal const val ID_SEARCH_FIELD_NAME = "id"
internal const val TITLE_SEARCH_FIELD_NAME = "title"

/** Defines a text-search service. */
interface TextSearchService {
  /** Populates the underlying text search service with data. */
  fun populateIndex()

  /**
   * Searches the timeline data for events whose description or title matches the provided query
   * string.
   *
   * @param query The text search query string.
   * @param minScore The minimum score of the match to accept in the final result set
   * @param offset The offset into the result. Used for pagination and defaults to 0.
   * @param limit The number of events to include in the result. Used for pagination and defaults
   *   to 10.
   * @return A potentially paginated list of [TimelineDataSearchResult]s that include the score of
   *   the match and the matching [TimelineData] event. This list is sorted by distance from highest
   *   (most relevant) to lowest (least relevant).
   */
  fun search(
    query: String,
    minScore: Float,
    offset: Int = 0,
    limit: Int = 10,
  ): List<TimelineDataSearchResult>
}

/**
 * Implementation of the [TextSearchService] that uses an in-memory Lucene index to store data and
 * query.
 */
@SuppressFBWarnings("BC_BAD_CAST_TO_ABSTRACT_COLLECTION", "EI_EXPOSE_REP2")
class LuceneTextSearchService(
  private val analyzer: Analyzer,
  private val directory: Directory,
  private val timelineDataLoader: TimelineDataLoader,
) : TextSearchService {
  private val parser = QueryParser(DESCRIPTION_SEARCH_FIELD_NAME, analyzer)

  override fun populateIndex() {
    IndexWriter(directory, IndexWriterConfig(analyzer)).use { writer ->
      timelineDataLoader.getTimelineData().forEachIndexed { index, event ->
        writer.addDocument(event.toDocument(index))
      }
    }
  }

  override fun search(
    query: String,
    minScore: Float,
    offset: Int,
    limit: Int,
  ): List<TimelineDataSearchResult> {
    logger.debug {
      "Searching for query '$query' with minimum score $minScore, offset $offset and limit $limit..."
    }
    return DirectoryReader.open(directory).use { indexReader ->
      val query = parser.parse(query)
      val searcher = IndexSearcher(indexReader)

      // Find all hits so that we can paginate the results
      val hits = searcher.search(query, timelineDataLoader.getTimelineData().size)

      hits.scoreDocs
        .toList()
        .map { d ->
          val doc = indexReader.storedFields().document(d.doc)
          val index = doc.get(ID_SEARCH_FIELD_NAME).toInt()
          val t = timelineDataLoader.getTimelineData()[index]
          TimelineDataSearchResult(d.score, t)
        }.paginate(limit = limit, offset = offset)
        .filter { it.score >= minScore }
        .sortedByDescending { it.score }
    }
  }
}

/**
 * Extension function to add pagination functionality to the list of timeline data.
 *
 * @param limit The maximum number of elements to retrieve from the list
 * @param offset The page "offset" into the list. In other words, the amount of pages to page into
 *   the list to find the requested sublist.
 * @return Returns the requested "page" of "limit" elements from the list. If the size exceeds the
 *   list size (in other words, the last page is requested), the remaining elements in the list are
 *   returned. If the start of the requested page exceeds the size of the list, an empty list is
 *   returned to indicate that there are no more pages available.
 */
private fun <T> List<T>.paginate(
  limit: Int,
  offset: Int,
): List<T> {
  val startIndex = limit * offset
  if (startIndex >= this.size) {
    return emptyList()
  }
  val endIndex = (startIndex + limit).coerceAtMost(this.size)
  return this.subList(startIndex, endIndex)
}

/** Extension function that converts [TimelineData] to a Lucene [Document]. */
private fun TimelineData.toDocument(id: Int): Document {
  val document = Document()
  document.add(IntField(ID_SEARCH_FIELD_NAME, id, Field.Store.YES))
  document.add(TextField(DESCRIPTION_SEARCH_FIELD_NAME, this.description, Field.Store.YES))
  document.add(TextField(TITLE_SEARCH_FIELD_NAME, this.title, Field.Store.YES))
  return document
}
