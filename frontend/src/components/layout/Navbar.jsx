import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../ui/button';
import { LogOut, User, GraduationCap } from 'lucide-react';

export function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className="border-b bg-white/75 backdrop-blur-md sticky top-0 z-50">
            <div className="container mx-auto flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
                <Link to="/" className="flex items-center space-x-2">
                    <GraduationCap className="h-6 w-6 text-blue-600" />
                    <span className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                        SPS Nurul Ikhlas
                    </span>
                </Link>
                <div className="flex items-center space-x-4">
                    {user ? (
                        <>
                            <span className="hidden sm:inline-block text-sm font-medium text-gray-700">
                                Hi, {user.username || 'User'}
                            </span>
                            <Button variant="ghost" size="sm" onClick={handleLogout}>
                                <LogOut className="mr-2 h-4 w-4" />
                                Logout
                            </Button>
                        </>
                    ) : (
                        <Link to="/login">
                            <Button variant="ghost" size="sm">
                                <User className="mr-2 h-4 w-4" />
                                Login
                            </Button>
                        </Link>
                    )}
                </div>
            </div>
        </nav>
    );
}
