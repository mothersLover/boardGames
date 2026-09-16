import React from 'react';
import { Link } from "react-router-dom";
import { Routes, Route, Navigate } from "react-router-dom";
import './index.css';
import Home from "./pages/Home";
import GamePage from "./pages/GamePage";
import SessionResults from "./pages/SessionResults";
import Multimedia from "./components/Multimedia";
import Statistics from "./components/Statistics";
import Rules from "./components/Rules";
import ActiveSessions from "./components/ActiveSessions";
import AdminLogin from "./admin/AdminLogin";
import AdminLayout from "./admin/AdminLayout";
import AdminGames from "./admin/AdminGames";
import AdminPlayers from "./admin/AdminPlayers";
import AdminNews from "./admin/AdminNews";
import RequireAdmin from "./admin/RequireAdmin";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/game/:gameId" element={<GamePage />} />
      <Route path="/game/:gameId/sessions" element={<ActiveSessions />} />
      <Route path="/game/:gameId/multimedia" element={<Multimedia />} />
      <Route path="/game/:gameId/statistics" element={<Statistics />} />
      <Route path="/game/:gameId/rules" element={<Rules />} />
      <Route path="/session/:sessionId" element={<SessionResults />} />

      <Route path="/admin/login" element={<AdminLogin />} />
      <Route
        path="/admin"
        element={
          <RequireAdmin>
            <AdminLayout />
          </RequireAdmin>
        }
      >
        <Route index element={<Navigate to="games" replace />} />
        <Route path="games" element={<AdminGames />} />
        <Route path="players" element={<AdminPlayers />} />
        <Route path="news" element={<AdminNews />} />
      </Route>
    </Routes>
  );
}


export default App;
