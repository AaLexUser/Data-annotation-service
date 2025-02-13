import { QueryProvider } from '@/constant/queryClient';
import routes from './routes';

export default function App() {
    return (
        <QueryProvider>
            <RouterContainer routes={routes} basename="/operator" />
        </QueryProvider>
    );
}