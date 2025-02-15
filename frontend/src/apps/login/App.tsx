import RouterContainer from '@/components/RouterContainer';
import AppContainer from '@/components/AppContainer';
import { QueryProvider } from '@/constant/queryClient';
import routes from './routes';

export default function App() {
  return (
    <QueryProvider>
      <AppContainer>
        <RouterContainer routes={routes} />
      </AppContainer>
    </QueryProvider>
  );
}
