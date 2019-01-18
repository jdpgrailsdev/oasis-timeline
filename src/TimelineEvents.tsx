import * as React from "react";
import { TimelineEvent } from "react-event-timeline";

export default class TimelineEvents extends React.Component<any, any> {

    constructor(props: any) {
        super(props);
        this.state = {
            timelineEvents: [
                {
                    description: <span>First gig as Oasis at The Boardwalk, Manchester, UK.  At this point, the band is a 4-piece made up of Liam Gallagher, Paul "Bonehead" Arthurs, Paul "Guigsy" McGuigan and Tony McCarroll.</span>,
                    timestamp: 'August 18, 1991',
                    title: 'First gig as Oasis',
                    type: 'gigs',
                    year: '1991'
                },
                {
                    description: <span>First gig with Noel Gallagher at the Boardwalk in Manchester, UK.</span>,
                    timestamp: 'October 19, 1991',
                    title: 'First gig with Noel Gallagher',
                    type: 'gigs',
                    year: '1991'
                },
                {
                    description: <span>Early recording session at Mark Coyle's home studio on Mauldeth Road West in Manchester, UK over various dates from late 1992 to early 1993.  Songs recorded in these sessions include <i>D'Yer Wanna Be a Spaceman?</i>, <i>Going Nowhere</i>, <i>Hello</i>, <i>Married With Children</i>, <i>Rockin' Chair</i> and <i>She's Electric</i>.</span>,
                    timestamp: 'Late 1992/Early 1993',
                    title: 'Recording Session, Mark Coyle Home Studio, Manchester, UK',
                    type: 'recordings',
                    year: '1992'
                },
                {
                    description: <span>Recording session at the Porter Street Studio in Liverpool, UK that produced the "<a href="https://en.wikipedia.org/wiki/Live_Demonstration" target="_blank">Live Demonstration"</a> tape.</span>,
                    timestamp: 'Spring 1993',
                    title: 'Real People Recording Session, Liverpool, UK',
                    type: 'recordings',
                    year: '1993'
                },
                {
                    description: <span>Oasis forces their way on to the bill at <a href="https://www.kingtuts.co.uk" target="_blank">King Tut's Wah Wah Hut</a> in Glasgow, Scotland.  They play four songs: <i>Rock 'n' Roll Star</i>, <i>Bring It On Down</i>, <i>Up In the Sky</i> and <i>I Am The Walrus</i>.  Alan McGee, the head of Creation Records, is in attendance and offers the band a recording contract on the spot.</span>,
                    timestamp: 'May 31, 1993',
                    title: 'King Tut\'s Wah Wah Hut, Glasgow, Scotland, UK',
                    type: 'gigs',
                    year: '1993'
                },
                {
                    description: <span>Recording session at Loco Residential Recording Studios, Newport, Wales, UK.  Songs recorded at this session include <i>Live Forever</i> and <i>Up In The Sky</i>.</span>,
                    timestamp: 'September, 1993',
                    title: 'Recording Session',
                    type: 'recordings',
                    year: '1993'
                },
                {
                    description: <span>Broadcast of Oasis's first ever live radio session on the BBC 5 show "Hit The North".  The session was recorded on August 11, 1993.  The band played the following songs: <i>Bring It On Down</i>, <i>I Will Believe</i>, <i>Digsy's Dinner</i>, <i>Cigarettes & Alcohol</i> and <i>Rock 'n' Roll Star</i>.</span>,
                    timestamp: 'September 17, 1993',
                    title: 'First Live Radio Broadcast',
                    type: 'noteworthy',
                    year: '1993'
                },
                {
                    description: <span><i>Shakermaker</i> recording session at Out of the Blue Studios, Manchester, UK.  This session produced the version that includes the "I'd like to teach the world to sing..." lyrics that would later be re-recorded prior to release as a single.</span>,
                    timestamp: 'October, 1993',
                    title: 'Shakermaker Recording Session',
                    type: 'recordings',
                    year: '1993'
                },
                {
                    description: <span>What begins as a session to record <i>Bring It On Down</i> as Oasis's first single ends up producing <i>Supersonic</i> at the <a href="https://milocostudios.com/studios/the-motor-museum/intro/" target="_blank">Pink Museum</a> in Liverpool, UK.  <i>Take Me Away</i> and an unreleased version of <i>I Will Believe</i> are also recorded during this session.</span>,
                    timestamp: 'December 19, 1993',
                    title: 'Supersonic Recording Session',
                    type: 'recordings',
                    year: '1994'
                },
                {
                    description: <span>First <i>Definitely Maybe</i> recording session begins at <a href="http://www.monnowvalleystudio.com/" target="_blank">Monnow Valley Residential Recording Studio</a> in Rockfield, Monmouthshire, Wales, UK.  The session lasts until January 26, 1994.</span>,
                    timestamp: 'January 7, 1994',
                    title: 'First Definitely Maybe Recording Session',
                    type: 'recordings',
                    year: '1994'
                },
                {
                    description: <span>Widely regarded as Oasis' big breakthrough gig in London and the first gig of 1994.  Performed at the legendary <a href="http://www.thewaterratsvenue.london/" target="_blank">Water Rats</a> venue.</span>,
                    timestamp: 'January 27, 1994',
                    title: 'Splash Club, Water Rats, Kings Cross, London, UK',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>First gig professionally recorded in full, it was given to a room full of Sony music executives at the Gleneagles Hotel prior to having released any music.</span>,
                    timestamp: 'February 6, 1994',
                    title: 'Geneagles Hotel, Pershire, Scotland, UK',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>On their way to Amsterdam, Netherlands to play their first foreign gig in support of The Verve, Liam, Bonhead, Guigsy and Tony are arrested on the ferry ride over and deported.</span>,
                    timestamp: 'February 17, 1994',
                    title: 'Amsterdam Overnight Ferry Incident',
                    type: 'noteworthy',
                    year: '1994'
                },
                {
                    description: <span>Second recording session for <i>Definitely Maybe</i> begins at <a href="https://sawmills.co.uk/" target="_blank">Sawmills Residential Recording Studios</a> in Cornwall, UK.  This session would produce most of the tracks for the album.  The session lasts until March 3, 1994.</span>,
                    timestamp: 'February 24, 1994',
                    title: 'Second Definitely Maybe Recording Session',
                    type: 'recordings',
                    year: '1994'
                },
                {
                    description: <span>Broadcast of Oasis's first ever live TV appearance on Channel 4's The Word.  The band played a shortened version of <i>Supersonic</i>.</span>,
                    timestamp: 'March 18, 1994',
                    title: 'First Live TV Appearance',
                    type: 'noteworthy',
                    year: '1994'
                },
                {
                    description: <span>First single released by Creation Records.  The single includes the b-sides <i>Take Me Away</i>, <i>I Will Believe</i> and a demo of <i>Columbia</i> recorded in 1993.</span>,
                    timestamp: 'April 11, 1994',
                    title: 'Supersonic Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>First appearance on the cover of the New Musical Expression magazine.  Liam is pictured standing in front of The Oasis Bar with the caption: "Totaly Cool - Oasis: What the world is wating for".</span>,
                    timestamp: 'June 4, 1994',
                    title: 'First NME Cover',
                    type: 'noteworthy',
                    year: '1994'
                },
                {
                    description: <span>Second single from <i>Definitely Maybe</i> released by Creation Records.  The single includes the b-sides <i>D'Yer Wanna Be a Spaceman?</i>, <i>Alive (8-track demo)</i> and <i>Bring It On Down (live)</i> recorded on August 11, 1993 for the BBC 5 radio show "Hit The North".</span>,
                    timestamp: 'June 13, 1994',
                    title: 'Shakermaker Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>First international gig.</span>,
                    timestamp: 'June 16, 1994',
                    title: 'Erotika, Paris, France',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>First appearance at the Glastonbury Festival.</span>,
                    timestamp: 'June 26, 1994',
                    title: 'Glastonbury Festival, Worthy Farm, Shepton Mallet, Pilton, UK',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>Oasis appears on the BBC TV show Top of the Pops for the first time.  The band "performs" <i>Shakermaker</i>.</span>,
                    timestamp: 'June 29, 1994',
                    title: 'First Appearance on Top of the Pops',
                    type: 'noteworthy',
                    year: '1994'
                },
                {
                    description: <span>First gig in the United States.</span>,
                    timestamp: 'July 21, 1994',
                    title: 'Wetlands Preserve Festival, New York, New York, USA',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>Third single from <i>Definitely Maybe</i> released by Creation Records.  The single includes the b-sides <i>Up in the Sky (acoustic)</i>, <i>Cloudburst</i> and <i>Supersonic (live)</i> recorded on February 6, 1994 at Gleneagles Hotel, Perth, Scotland, UK.</span>,
                    timestamp: 'August 8, 1994',
                    title: 'Live Forever Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>Debut studio album released by Creation Records.  It would go on to become the fastest selling debut album in UK chart history at the time.</span>,
                    timestamp: 'August 29, 1994',
                    title: 'Definitely Maybe Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>First gig in Japan.</span>,
                    timestamp: 'September 13, 1994',
                    title: 'Quattro, Tokyo, Japan',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>Infamous gig that resulted in Noel Gallagher leaving the band.  He would rejoin them about a week later and continue the tour.</span>,
                    timestamp: 'September 29, 1994',
                    title: 'The Whiskey A Go Go, Los Angeles, California, USA',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>Recording session at the <a href="http://congresshouse.com/" target="_blank">Congress House Studio</a> in Austin, TX while on tour in the US during September/October, 1994.  <i>(It's Good) To Be Free</i>, <i>Talk Tonight</i> and <i>Half the World Away</i> are recorded.</span>,
                    timestamp: 'October ?, 1994',
                    title: 'Recording Session at Congress House, Austin, Texas, USA',
                    type: 'recordings',
                    year: '1994'
                },
                {
                    description: <span>Fourth single from <i>Definitely Maybe</i> released by Creation Records.  The single includes the b-sides <i>I Am The Walrus (live)</i>, <i>Listen Up</i> and <i>Fade Away</i>.</span>,
                    timestamp: 'October 10, 1994',
                    title: 'Cigarettes & Alcohol Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>Professionally recorded show for radio broadcast.  Available as a <a href="https://www.discogs.com/Oasis-Live/release/1622979" target="_blank">promotional release</a> for US radio stations.</span>,
                    timestamp: 'October 15, 1994',
                    title: 'Cabaret Metro, Chicago, Illinois, USA',
                    type: 'gigs',
                    year: '1994'
                },
                {
                    description: <span>Recording session for the <i>Whatever</i> single at <a href="http://www.rockfieldmusicgroup.com/default.asp?contentID=543" target="_blank">Rockfield Studios</a> in Monmouth, Wales, UK.</span>,
                    timestamp: 'November, 1994',
                    title: 'Whatever Recording Session',
                    type: 'recordings',
                    year: '1994'
                },
                {
                    description: <span>Stand-alone single released between the albums <i>Definitely Maybe</i> and <i>(What's the Story) Morning Glory?</i>.  The single includes the b-sides <i>(It's Good) To Be Free</i>, <i>Half the World Away</i> and <i>Slide Away</i>.</span>,
                    timestamp: 'December 18, 1994',
                    title: 'Whatever Released',
                    type: 'releases',
                    year: '1994'
                },
                {
                    description: <span>Concert released as the official video <i>Live By The Sea</i>.  One of Tony McCarrol's last appearances with the band.</span>,
                    timestamp: 'April 17, 1995',
                    title: 'Cliffs Pavilion, Southend, UK',
                    type: 'gigs',
                    year: '1995'
                }
            ]
        };
    }

    createTimelineEvents = () => {
        let events = []

        for(var i in this.state.timelineEvents) {
            const event = this.state.timelineEvents[i];
            const className = event.type + " " + event.year;

            events.push(
<div className={className}>
    <TimelineEvent
        title={event.title}
        createdAt={event.timestamp}
        icon={ <i className="material-icons md-18">{this.getIcon(event.type)}</i> }
        contentStyle={{ fontFamily: 'Roboto' }}
    >
        {event.description}
    </TimelineEvent>
</div>);
        }

        return events;
    }

    getIcon(type: any) {
        switch(type) {
          case 'gigs':
             return 'speaker';
          case 'noteworthy':
             return 'announcement';
          case 'recordings':
             return 'settings_voice';
          case 'releases':
          default:
             return 'music_note';
        }
    }

    render() {
      return(
          <div>
              {this.createTimelineEvents()}
          </div>
      )
    }
}