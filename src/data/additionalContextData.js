export default {

    data: {
        "Autumn, 1991_recordings" : [
            "Alice",
            "Take Me",
            "Reminice (sic)"
        ],
        "October 19, 1991_gigs" : [
            "Columbia (instrumental version)",
            "Take Me",
            "Life in Vain (aka Acoustic)",
            "Better Let You Know"
        ],
        "Spring, 1993_recordings" : [
            "D'Yer Wanna Be a Spaceman?'",
            "Going Nowhere",
            "Hello",
            "Married With Children (version included on Definitely Maybe)",
            "Rockin' Chair'",
            "She's Electric'"
        ],
        "March, 1993_recordings" : [
            "Bring It On Down",
            "Cloudburst",
            "Columbia",
            "Fade Away",
            "Rock 'n' Roll Star",
            "Strange Thing"
        ],
        "May 31, 1993_gigs" : [
            "Rock 'n' Roll Star",
            "Bring It On Down",
            "Up In the Sky",
            "I Am The Walrus"
        ],
        "September, 1993_recordings" : [
            "Digsy's Dinner",
            "Live Forever",
            "Up In The Sky"
        ],
        "December 19, 1993_recordings" : [
            "Bring It On Down",
            "Supersonic",
            "Take Me Away",
            "I Will Believe (unreleased version)"
        ],
        "May 8, 1995_recordings" : [
            "Roll With It",
            "Hello",
            "Wonderwall",
            "Don't Look Back In Anger",
            "Champagne Supernova"
        ]
    },

    generateKey: function(timestamp, type) {
        return timestamp + "_" + type;
    },

    hasAdditionalContext: function(timestamp, type) {
        const key = this.generateKey(timestamp, type);
        return this.data.hasOwnProperty(key);
    }
};