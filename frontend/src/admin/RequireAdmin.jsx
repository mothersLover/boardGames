import { Navigate } from "react-router-dom";
import { isLoggedIn } from "./adminApi";

export default function RequireAdmin({ children }) {
  if (!isLoggedIn()) {
    return <Navigate to="/admin/login" replace />;
  }
  return children;
}
