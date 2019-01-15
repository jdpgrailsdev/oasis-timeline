// https://www.npmjs.com/package/react-event-timeline
// https://google.github.io/material-design-icons/

import * as React from "react";
import { render } from "react-dom";
import Timeline from "./Timeline";

const App = () => (
	<div>
  <h1>Oasis Timeline</h1>
  <div>
    <Timeline />
  </div>
  </div>
);

render(<App />, document.getElementById("root"));