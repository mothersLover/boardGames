
import { Link } from "react-router-dom";
import logo7Wonders from '../resources/7.png';
import logoChampions from '../resources/champions.png';
import logoCythe from '../resources/scythe.png';

export default function Games() {
  return (
        <section className="games">
          <Link to="/game/7wonders" className="game small">
            <img src={logo7Wonders} alt="7 Wonders" />
            <span className="game-title">7 Wonders</span>
          </Link>

          <Link to="/game/champions" className="game small">
            <img src={logoChampions} alt="Champions of Midgard" />
            <span className="game-title">Champions of Midgard</span>
          </Link>

          <Link to="/game/scythe" className="game large">
            <img src={logoCythe} alt="Scythe" />
            <span className="game-title">Scythe</span>
          </Link>
        </section>
  );
}
