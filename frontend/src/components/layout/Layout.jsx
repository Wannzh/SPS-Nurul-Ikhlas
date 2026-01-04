import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';

export function Layout() {
    return (
        <div className="min-h-screen bg-gray-50 font-sans">
            <Navbar />
            <main className="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <Outlet />
            </main>
            <footer className="bg-white border-t mt-auto py-6">
                <div className="container mx-auto px-4 text-center text-gray-500 text-sm">
                    &copy; {new Date().getFullYear()} SPS Nurul Ikhlas. All rights reserved.
                </div>
            </footer>
        </div>
    );
}
