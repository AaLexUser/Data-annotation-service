import { QueryProvider } from '@/constant/queryClient';
import RouterContainer from '@/components/RouterContainer';
import routes from './routes';

export default function App() {
  return (
    <QueryProvider>
      <RouterContainer routes={routes} basename="/admin" />
    </QueryProvider>
  );
}
