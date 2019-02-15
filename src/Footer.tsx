import * as React from "react";

class Footer extends React.Component<any, any> {

    render() {
        return(
            <div id="footer">
                <br />
                <br />
                <br />
                <br />
                <br />
                <footer>
                    <span>&copy; 2019</span>
                    <span>Last Updated: {process.env.REACT_APP_UPDATED_AT}</span>
                </footer>
            </div>
        );
    }
}

export default Footer;