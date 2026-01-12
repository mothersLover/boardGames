import React from 'react';
import { Link } from "react-router-dom";
import { Routes, Route } from "react-router-dom";
import './index.css';
import Home from "./pages/Home";
import GamePage from "./pages/GamePage";
import Multimedia from "./components/Multimedia";
import Statistics from "./components/Statistics";
import Rules from "./components/Rules";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/game/:gameId" element={<GamePage />} />
      <Route path="/game/:gameId/multimedia" element={<Multimedia />} />
      <Route path="/game/:gameId/statistics" element={<Statistics />} />
      <Route path="/game/:gameId/rules" element={<Rules />} />
    </Routes>
  );
}


export default App;
