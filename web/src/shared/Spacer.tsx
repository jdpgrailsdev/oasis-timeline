import * as React from "react";

export default class Spacer extends React.Component<any, any> {

    render() {
        const divStyle = {
            borderBottom: '3px solid black',
            display: 'block',
            margin: '0 auto',
            marginBottom: '10px',
            width: '65%'
        };

        return(
            <div data-testid="spacer-test">
                <br />
                <br />
                <div style={divStyle}></div>
            </div>
        );
    }
}