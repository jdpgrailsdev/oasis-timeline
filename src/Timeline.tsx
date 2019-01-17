import * as React from "react";
import { Timeline, TimelineEvent } from "react-event-timeline";

export default () => {
  return (
    <Timeline>
        {/*1991*/}
        <div className="gigs">
            <TimelineEvent
                title="First gig as Oasis"
                createdAt="August 18, 1991"
                icon={<i className="material-icons md-18">speaker</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
            >
                First gig as Oasis at The Boardwalk, Manchester, UK.
            </TimelineEvent>
        </div>

        {/*1992*/}
        <div className="recordings">
            <TimelineEvent
                title="Recording Session, Mark Coyle Home Studio, Manchester, UK"
                createdAt="Late 1992/Early 1993"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                Early recording session at Mark Coyle's home studio on Mauldeth Road West in Manchester, UK over
                various dates from late 1992 to early 1993.  Songs recorded in these sessions include <i>D'Yer Wanna Be a Spaceman?</i>, <i>Going Nowhere</i>, <i>Hello</i>, <i>Married With Children</i>, <i>Rockin' Chair</i> and <i>She's Electric</i>.
            </TimelineEvent>
        </div>

        {/*1993*/}
        <div className="recordings">
            <TimelineEvent
                title="Real People Recording Session, Liverpool, UK"
                createdAt="Spring 1993"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                Recording session at the Porter Street Studio in Liverpool, UK that produced the "<a href="https://en.wikipedia.org/wiki/Live_Demonstration" target="_blank">Live Demonstration"</a> tape.
            </TimelineEvent>
        </div>
        <div className="gigs">
            <TimelineEvent
                title="King Tut's Wah Wah Hut, Glasgow, Scotland, UK"
                createdAt="May 31, 1993"
                icon={<i className="material-icons md-18">speaker</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
            >
                Oasis forces their way on to the bill at <a href="https://www.kingtuts.co.uk" target="_blank">King Tut's Wah Wah Hut</a> in Glasgow, Scotland.  They play four songs: <i>Rock 'n' Roll Star</i>, <i>Bring It On Down</i>, <i>Up In the Sky</i> and <i>I Am The Walrus</i>.
                Alan McGee, the head of Creation Records, is in attendance and offers the band a recording contract on the spot.
            </TimelineEvent>
        </div>
        <div className="recordings">
            <TimelineEvent
                title="Recording Session"
                createdAt="September, 1993"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                Recording session at Loco Residential Recording Studios, Newport, Wales, UK.  Songs recorded at this session include <i>Live Forever</i> and <i>Up In The Sky</i>.
            </TimelineEvent>
        </div>
        <div className="noteworthy">
            <TimelineEvent
                title="First Live Radio Broadcast"
                createdAt="September 17, 1993"
                icon={<i className="material-icons md-18">announcement</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
            >
                Broadcast of Oasis's first ever live radio session on the BBC 5 show "Hit The North".  The session was recorded on August 11, 1993.  The band played the following songs: <i>Bring It On Down</i>, <i>I Will Believe</i>, <i>Digsy's Dinner</i>, <i>Cigarettes & Alcohol</i> and <i>Rock 'n' Roll Star</i>.
            </TimelineEvent>
        </div>
        <div className="recordings">
            <TimelineEvent
                title="Shakermaker Recording Session"
                createdAt="October, 1993"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                <i>Shakermaker</i> recording session at Out of the Blue Studios, Manchester, UK.  This session produced the version that includes the "I'd like to teach the world to sing..." lyrics that would later be re-recorded prior to release as a single.
            </TimelineEvent>
        </div>
        <div className="recordings">
            <TimelineEvent
                title="Supersonic Recording Session"
                createdAt="December 19, 1993"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                What begins as a session to record <i>Bring It On Down</i> as Oasis's first single ends up producing <i>Supersonic</i> at the <a href="https://milocostudios.com/studios/the-motor-museum/intro/" target="_blank">Pink Museum</a> in Liverpool, UK.
            </TimelineEvent>
        </div>

        {/*1994*/}
        <div className="recordings">
            <TimelineEvent
                title="First Definitely Maybe Recording Session"
                createdAt="January 7, 1994"
                icon={<i className="material-icons md-18">settings_voice</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                First <i>Definitely Maybe</i> recording session begins at <a href="http://www.monnowvalleystudio.com/" target="_blank">Monnow Valley Residential Recording Studio</a> in Rockfield, Monmouthshire, Wales, UK.
            </TimelineEvent>
        </div>
        <div className="noteworthy">
            <TimelineEvent
                title="Amsterdam Overnight Ferry Incident"
                createdAt="February 17, 1994"
                icon={<i className="material-icons md-18">announcement</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                  On their way to Amsterdam, Netherlands to play their first foreign gig in support of The Verve, Liam, Bonhead, Guigsy and Tony are arrested on the ferry ride over and deported.
              </TimelineEvent>
        </div>
        <div className="noteworthy">
            <TimelineEvent
                title="First Live TV Appearance"
                createdAt="March 18, 1994"
                icon={<i className="material-icons md-18">announcement</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
            >
                Broadcast of Oasis's first ever live TV appearance on Channel 4's The Word.  The band played a shortened version of <i>Supersonic</i>.
            </TimelineEvent>
        </div>
        <div className="releases">
            <TimelineEvent
                title="Supersonic Released"
                createdAt="April 11, 1994"
                icon={<i className="material-icons md-18">music_note</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                  First single released by Creation Records.  The single includes the b-sides <i>Take Me Away</i>, <i>I Will Believe</i> and a demo of <i>Columbia</i> recorded in 1993.
            </TimelineEvent>
        </div>
        <div className="releases">
            <TimelineEvent
                title="Definitely Maybe Released"
                createdAt="August 29, 1994"
                icon={<i className="material-icons md-18">music_note</i>}
                contentStyle={{
                    fontFamily: 'Roboto'
                }}
              >
                  Debut studio album released by Creation Records.
            </TimelineEvent>
        </div>
    </Timeline>
  );
};
