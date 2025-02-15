import Login from './pages/login';

export default [
  {
    path: '/',
    element: <Login />,
    // This ID can be used to get data from loader in routes
    id: 'root',
    handle: {
      crumb: () => {
        return 'DeepPick';
      },
    },
  },
  {
    path: '/login',
    element: <Login />,
    // This ID can be used to get data from loader in routes
    id: 'login',
    handle: {
      crumb: () => {
        return 'DeepPick';
      },
    },
  },
];
