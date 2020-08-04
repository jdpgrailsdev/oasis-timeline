package com.jdpgrailsdev.oasis.timeline.data

import spock.lang.Specification
import twitter4j.TwitterException

class TweetSpec extends Specification {

    def "test that when a tweet is created for blank status text, an exception is raised"() {
        when:
            new Tweet(null)
        then:
            thrown TwitterException
        when:
            new Tweet('')
        then:
            thrown TwitterException
    }

    def "test that when the main tweet is retrieved, the first tweet in the underlying collection is returned"() {
        setup:
            def tweet = new Tweet('text')
            tweet.@messages[0] = 'The main tweet'
            tweet.@messages[1] = 'Reply 1'
            tweet.@messages[2] = 'Reply 2'
            tweet.@messages[3] = 'Reply 3'
        when:
            def mainTweet = tweet.getMainTweet()
        then:
            mainTweet.getStatus() == tweet.messages.first()
        when:
            def messages = tweet.getMessages()
        then:
            messages.size() == 4
    }

    def "test that when the replies for the tweet are retrieved, the first tweet in the underlying collection is skipped and all other messages are returned with the reply ID set"() {
        setup:
            def tweet = new Tweet('text')
            tweet.@messages[0] = 'The main tweet'
            tweet.@messages[1] = 'Reply 1'
            tweet.@messages[2] = 'Reply 2'
            tweet.@messages[3] = 'Reply 3'
            def messageId = 12345l
        when:
            def replies = tweet.getReplies(messageId)
        then:
            replies.size() == 3
            replies.find { it.getStatus() == tweet.messages.first() } == null
            replies.each { reply ->
                reply.getInReplyToStatusId() == messageId
            }
        when:
            def messages = tweet.getMessages()
        then:
            messages.size() == 4
    }

    def "test that when an event that exceeds the limit of characters is appropriately broken up into individual parts"() {
        setup:
            def text = '#OnThisDay in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The band refuse to continue the show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live Forever, Bring It On Down (Noel Gallagher attacked on stage during song).'
        when:
            def tweet = new Tweet(text)
        then:
            tweet.messages.size() == 3
            tweet.messages[0].length() <= Tweet.TWEET_LIMIT
            tweet.messages[0] == '#OnThisDay in 1994, after back and forth with fans during a gig at Riverside in Newcastle, UK, a fight breaks out on stage resulting in Noel Gallager damaging a 1960\'s sunburst Gibson Les Paul guitar given to him by Johnny Marr of The Smiths.  The...'
            tweet.messages[1].length() <= Tweet.TWEET_LIMIT
            tweet.messages[1] == '... band refuse to continue the show after 5 songs, leading to fans surrounding the band\'s van.  Noel also would require stitches after the attack.  The setlist includes the following songs: Columbia, Shakermaker, Fade Away, Digsy\'s Dinner, Live...'
            tweet.messages[2].length() <= Tweet.TWEET_LIMIT
            tweet.messages[2] == '... Forever, Bring It On Down (Noel Gallagher attacked on stage during song).'
    }

    def "test that when an event exceeds the limit but the split part ends a sentence, the tweet is appropriately broken up into individual parts without elipses"() {
        setup:
            def text = "${TimelineDataType.gigs.getEmoji()} #OnThisDay in 1991, @Oasis perform their first gig under the name \"@Oasis\" at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony McCarroll.  The Inspiral Carpets are in attendance, accompanied by roadie Noel Gallagher, who sees his brother\'s band perform live for the first time.\n\n@liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls #Oasis #TodayInMusic #britpop"
        when:
            def tweet = new Tweet(text)
        then:
            tweet.messages.size() == 2
            tweet.messages[0].length() <= Tweet.TWEET_LIMIT
            tweet.messages[0] == "${TimelineDataType.gigs.getEmoji()} #OnThisDay in 1991, @Oasis perform their first gig under the name \"@Oasis\" at The Boardwalk in Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, Paul \"Bonehead\" Arthurs, Paul \"Guigsy\" McGuigan and Tony McCarroll."
            tweet.messages[1].length() <= Tweet.TWEET_LIMIT
            tweet.messages[1] == 'The Inspiral Carpets are in attendance, accompanied by roadie Noel Gallagher, who sees his brother\'s band perform live for the first time.\n\n@liamGallagher @noelgallagher @boneheadspage @TonyMcCarrolls #Oasis #TodayInMusic #britpop'
    }
}
