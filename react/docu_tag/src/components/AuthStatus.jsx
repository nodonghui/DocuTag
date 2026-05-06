import { fetchLogout } from "../api/JwtApi";
import "../styles/DocuTag.css";
export default function AuthStatus({ user, onLogout }) {
    
    if (!user) return null;
    
    return (
        <div className="auth-status">
            <span className="auth-status__name">{user.userName}님</span>
            <button className="auth-status__button" onClick={() => fetchLogout({user, onLogout})}>
                로그아웃
            </button>
        </div>
    );

}