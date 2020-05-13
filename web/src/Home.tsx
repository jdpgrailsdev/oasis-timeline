import * as React from "react";
import TodayInHistory from "./shared/TodayInHistory";

export default class Home extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top">
                <br />
                <br />
                <div className="quote group">
                    <div className="quote-container">
                        <blockquote>
                            <p>This is history!  Right here, right now - this is history!</p>
                        </blockquote>
                        <cite><span>Noel Gallagher</span><br />
                            Knebworth, Hertfordshire, UK<br />
                            August 10, 1996
                        </cite>
                    </div>
                </div>
                <TodayInHistory />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
                <br />
            </div>
        );
    }
}