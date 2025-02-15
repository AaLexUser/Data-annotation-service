import RouterContainer from '@/components/RouterContainer';
import { QueryProvider } from '@/constant/queryClient';
import routes from './routes';

export default function App() {
  return (
    <QueryProvider>
      <RouterContainer routes={routes} />
    </QueryProvider>
  );
}
