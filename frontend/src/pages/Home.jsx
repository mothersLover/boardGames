import logo from '../resources/logo.jpg';
import News from "../components/News";
import Games from "../components/Games";

export default function Home() {
  return (
    // <main className="main">
    //   <News />
    //   <Games />
    // </main>
    <>
          <header className="header">
            <div className="logo">
              <img src={logo} alt="logo" />
              <div className="logoText">
                <h1>Do you wanna play?</h1>
                <p>Let's play!</p>
              </div>
            </div>
          </header>
    
          <main className="main">
            {/* News Section */}
            <News />
    
            {/* Games Gallery using React Router Links */}
            <Games />
          </main>
        </>
  );
}
