import * as React from "react";
import BackToTop from "./shared/BackToTop";
import Spacer from "./shared/Spacer";

export default class Terms extends React.Component<any, any> {

    render() {
        return(
            <div className="main" id="top">
                <h2>Terms</h2>
                <br />
                <div className="mainText">
                    THE USE OF THIS WEBSITE IS SUBJECT TO THE FOLLOWING TERMS AND CONDITIONS.  ANYONE USING THIS WEBSITE IS DEEMED TO HAVE READ AND AGREED TO THESE TERMS.
                </div>
                <Spacer />
                <h3 id="terms">Terms and Conditions</h3>
                <p className="mainText">These terms and conditions outline the rules and regulations for the use of The Oasis Timeline's Website, located at https://www.oasis-timeline.com.</p>
                <p className="mainText">By accessing this website we assume you accept these terms and conditions. Do not continue to use The Oasis Timeline if you do not agree to take all of the terms and conditions stated on this page.</p>
                <p className="mainText">The following terminology applies to these Terms and Conditions, Privacy Statement and Disclaimer Notice and all Agreements: "Client", "You" and "Your" refers to you, the person log on this website and compliant to the Company’s terms and conditions. "The Company", "Ourselves", "We", "Our" and "Us", refers to our Company. "Party", "Parties", or "Us", refers to both the Client and ourselves. All terms refer to the offer, acceptance and consideration of payment necessary to undertake the process of our assistance to the Client in the most appropriate manner for the express purpose of meeting the Client’s needs in respect of provision of the Company’s stated services, in accordance with and subject to, prevailing law of Netherlands. Any use of the above terminology or other words in the singular, plural, capitalization and/or he/she or they, are taken as interchangeable and therefore as referring to same.</p>
                <Spacer />
                <h3>Hyperlinking to our Content</h3>
                <p className="mainText">The following organizations may link to our Website without prior written approval:</p>
                <ul className="mainText">
                    <li>Government agencies;</li>
                    <li>Search engines;</li>
                    <li>News organizations;</li>
                    <li>Online directory distributors may link to our Website in the same manner as they hyperlink to the Websites of other listed businesses; and</li>
                    <li>System wide Accredited Businesses except soliciting non-profit organizations, charity shopping malls, and charity fundraising groups which may not hyperlink to our Web site.</li>
                </ul>
                <p className="mainText">These organizations may link to our home page, to publications or to other Website information so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products and/or services; and (c) fits within the context of the linking party’s site.</p>
                <p className="mainText">We may consider and approve other link requests from the following types of organizations:</p>
                <ul className="mainText">
                    <li>commonly-known consumer and/or business information sources;</li>
                    <li>dot.com community sites;</li>
                    <li>associations or other groups representing charities;</li>
                    <li>online directory distributors;</li>
                    <li>internet portals;</li>
                    <li>accounting, law and consulting firms; and</li>
                    <li>educational institutions and trade associations.</li>
                </ul>
                <p className="mainText">We will approve link requests from these organizations if we decide that: (a) the link would not make us look unfavorably to ourselves or to our accredited businesses; (b) the organization does not have any negative records with us; (c) the benefit to us from the visibility of the hyperlink compensates the absence of The Oasis Timeline; and (d) the link is in the context of general resource information.</p>
                <p className="mainText">These organizations may link to our home page so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products or services; and (c) fits within the context of the linking party’s site.</p>
                <p className="mainText">If you are one of the organizations listed in paragraph 2 above and are interested in linking to our website, you must inform us by sending an e-mail to The Oasis Timeline. Please include your name, your organization name, contact information as well as the URL of your site, a list of any URLs from which you intend to link to our Website, and a list of the URLs on our site to which you would like to link. Wait 2-3 weeks for a response.</p>
                <p className="mainText">Approved organizations may hyperlink to our Website as follows:</p>
                <ul className="mainText">
                    <li>By use of the uniform resource locator being linked to; or</li>
                    <li>By use of any other description of our Website being linked to that makes sense within the context and format of content on the linking party’s site.</li>
                </ul>
                <Spacer />
                <h3>iFrames</h3>
                <p className="mainText">Without prior approval and written permission, you may not create frames around our Webpages that alter in any way the visual presentation or appearance of our Website.</p>
                <Spacer />
                <h3>Content Liability</h3>
                <p className="mainText">We shall not be held responsible for any content that appears on your Website. You agree to protect and defend us against all claims that is rising on your Website. No link(s) should appear on any Website that may be interpreted as libelous, obscene or criminal, or which infringes, otherwise violates, or advocates the infringement or other violation of, any third party rights.</p>
                <Spacer />
                <h3>Reservation of Rights</h3>
                <p className="mainText">We reserve the right to request that you remove all links or any particular link to our Website. You approve to immediately remove all links to our Website upon request. We also reserve the right to amend these terms and conditions and it’s linking policy at any time. By continuously linking to our Website, you agree to be bound to and follow these linking terms and conditions.</p>
                <Spacer />
                <h3>Removal of links from our website</h3>
                <p className="mainText">If you find any link on our Website that is offensive for any reason, you are free to contact and inform us any moment. We will consider requests to remove links but we are not obligated to or so or to respond to you directly.</p>
                <p className="mainText">We do not ensure that the information on this website is correct, we do not warrant its completeness or accuracy; nor do we promise to ensure that the website remains available or that the material on the website is kept up to date.</p>
                <Spacer />
                <h3>Disclaimer</h3>
                <p className="mainText">The Oasis Timeline is an unofficial fan website and is not affiliated with Oasis, Epic Records, Sony Music, or Ignition Management.  All information contained within this site is soley for entertainment purposes and is used within the scope of "fair use" purposes.</p>
                <p className="mainText">The content of this site is provided on an "as is" basis and makes no representations or warranties of any kind, express or implied, with respect to this web site or information included in this site.  While we strive to maintain an accurate account of events, we do not warrant that the information accessible via this web site is accurate, complete and current.  Please see the <a href="#/sources">sources page</a> for a complete list of source material used to confirm the dates listed on this site.  The Oasis Timeline is not responsible for the content of source material and does not necessarily endorse the views expressed within.</p>
                <p className="mainText">As long as the website and the information and services on the website are provided free of charge, we will not be liable for any loss or damage of any nature.</p>
                <br />
                <br />
                <BackToTop baseUri="/terms" anchorId="top" />
                <br />
                <br />
                <br />
                <br />
            </div>
        );
    }
}
