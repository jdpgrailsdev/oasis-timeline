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

package com.jdpgrailsdev.oasis.timeline.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

const val LIMIT = 280

internal class PostTest {
  @Test
  fun testExceptionForBlankPost() {
    assertThrows(PostException::class.java) { Post.createPost(text = null, limit = LIMIT) }
    assertThrows(PostException::class.java) { Post.createPost(text = "", limit = LIMIT) }
  }

  @Test
  @Throws(PostException::class)
  fun testFirstPostRetrieved() {
    val text =
      (
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur " +
          "ullamcorper fringilla turpis a dapibus. Proin auctor feugiat rhoncus. Phasellus " +
          "id enim in ex pellentesque cursus sit amet vitae lorem. Aenean eget luctus odio, " +
          "vulputate luctus neque. Aenean non neque non enim laoreet semper. Ut mattis " +
          "lectus imperdiet rhoncus tincidunt. Nam vitae libero lorem. Aenean vulputate " +
          "turpis ac lacus aliquam, et vestibulum erat laoreet. Nullam pretium elit sit " +
          "amet dui maximus, tempor lobortis gravida."
      )

    val post = Post.createPost(text = text, limit = LIMIT)
    val mainPost = post.getMainPost()

    assertEquals(
      (
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ullamcorper fringilla" +
          " turpis a dapibus. Proin auctor feugiat rhoncus. Phasellus id enim in ex" +
          " pellentesque cursus sit amet vitae lorem. Aenean eget luctus odio, vulputate luctus" +
          " neque. Aenean..."
      ),
      mainPost,
    )

    val messages = post.getMessages()
    assertEquals(2, messages.size)
  }

  @Test
  @Throws(PostException::class)
  fun testSplittingLongPost() {
    val text =
      (
        "#OnThisDay in 1994, after back and forth with fans during a gig " +
          "at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel " +
          "Gallager damaging a 1960's sunburst Gibson Les Paul guitar given to him by " +
          "Johnny Marr of The Smiths.  The band refuse to continue the show after 5 songs, " +
          "leading to fans surrounding the band's van.  Noel also would require stitches " +
          "after the attack.  The setlist includes the following songs: Columbia, " +
          "Shakermaker, Fade Away, Digsy's Dinner, Live Forever, Bring It On Down " +
          "(Noel Gallagher attacked on stage during song)."
      )

    val post = Post.createPost(text = text, limit = LIMIT)

    assertEquals(3, post.getMessages().size)
    assertTrue(post.getMessages().first().length <= LIMIT)
    assertEquals(
      (
        "#OnThisDay in 1994, after back and " +
          "forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out " +
          "on stage resulting in Noel Gallager damaging a 1960's sunburst Gibson Les Paul " +
          "guitar given to him by Johnny Marr of The Smiths.  The..."
      ),
      post.getMessages().first(),
    )
    assertTrue(post.getMessages()[1].length <= LIMIT)
    assertEquals(
      (
        "... band refuse to continue the show after 5 " +
          "songs, leading to fans surrounding the band's van.  Noel also would require " +
          "stitches after the attack.  The setlist includes the following songs: Columbia, " +
          "Shakermaker, Fade Away, Digsy's Dinner, Live..."
      ),
      post.getMessages()[1],
    )
    assertTrue(post.getMessages().last().length <= LIMIT)
    assertEquals(
      "... Forever, Bring It On Down " + "(Noel Gallagher attacked on stage during song).",
      post.getMessages().last(),
    )
  }

  @Test
  @Throws(PostException::class)
  fun testSplitPostSentenceEnd() {
    val text =
      (
        """${TimelineDataType.GIGS.getEmoji(
          true,
        )} #OnThisDay in 1991, @Oasis perform their first gig under the name "@Oasis" at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, Paul "Bonehead" Arthurs, Paul "Guigsy" McGuigan and Tony McCarroll.  The Inspiral Carpets are in attendance, accompanied by roadie Noel Gallagher, who sees his brother's band perform live for the first time.

@liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls #Oasis #TodayInMusic #britpop"""
      )
    val tweet = Post.createPost(text = text, limit = LIMIT)

    assertEquals(2, tweet.getMessages().size)
    assertTrue(tweet.getMessages().first().length <= LIMIT)
    assertEquals(
      (
        TimelineDataType.GIGS.getEmoji(true) +
          " #OnThisDay " +
          "in 1991, @Oasis perform their first gig under the name \"@Oasis\" at The Boardwalk " +
          "in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, " +
          "Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony McCarroll."
      ),
      tweet.getMessages().first(),
    )
    assertTrue(tweet.getMessages().last().length <= LIMIT)
    assertEquals(
      (
        """
        The Inspiral Carpets are in attendance, accompanied by roadie Noel Gallagher, who sees his brother's band perform live for the first time.
        
        @liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls #Oasis #TodayInMusic #britpop
        """.trimIndent()
      ),
      tweet.getMessages().last(),
    )
  }
}
