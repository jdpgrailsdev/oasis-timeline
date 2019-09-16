export default {

    data: {
        "December 19, 1993_recordings" : [
            "Bring It On Down",
            "Supersonic"
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