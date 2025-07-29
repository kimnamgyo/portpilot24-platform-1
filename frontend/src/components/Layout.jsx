import { Outlet } from 'react-router-dom';
import Header from './Header';
import GlobalSnackbar from './GlobalSnackbar'; // Snackbar import

function Layout() {
  return (
    <div>
      <Header />
      <GlobalSnackbar /> {/* 앱 전체에 알림을 띄우기 위해 여기에 추가 */}
      <main>
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;