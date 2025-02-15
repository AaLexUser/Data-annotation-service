import MainLayout from './layouts/Main';
export default [
  {
    path: '/',
    element: <MainLayout />,
    // This ID can be used to get data from the loader in the route
    id: 'root',
    handle: {
      crumb: () => {
        return 'Task Management';
      },
    },
  },
];
