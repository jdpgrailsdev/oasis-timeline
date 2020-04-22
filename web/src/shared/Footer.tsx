import * as React from "react";
import MediaQuery from "react-responsive";

export default class Footer extends React.Component<any, any> {

    render() {
        return(
            <div id="footer" className="tableCaptionBottom">
                <footer>
                    <MediaQuery query="(min-device-width: 768px)">
                        <div>
                            <span>&copy; 2020</span>
                        </div>
                        <div>
                            <span>Last Updated: {process.env.REACT_APP_UPDATED_AT}</span>
                        </div>
                    </MediaQuery>
                    <MediaQuery query="(max-device-width: 767px)">
                        <span>&copy; 2020</span>
                        <span>Last Updated</span>
                        <span>{process.env.REACT_APP_UPDATED_AT}</span>
                    </MediaQuery>
                </footer>
            </div>
        );
    }
}