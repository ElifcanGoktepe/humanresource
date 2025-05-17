import { useEffect } from 'react';
import { Doughnut } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend
} from 'chart.js';

ChartJS.register(ArcElement, Tooltip, Legend);

type LeaveChartProps = {
    onDataReady?: (data: { total: number; used: number; remaining: number }) => void;
};

const LeaveChart = ({ onDataReady }: LeaveChartProps) => {
    const total = 20;
    const used = 12;
    const remaining = total - used;

    // Parent’a veriyi gönder
    useEffect(() => {
        if (onDataReady) {
            onDataReady({ total, used, remaining });
        }
    }, [onDataReady]);

    const data = {
        labels: ['Kullanılan', 'Kalan'],
        datasets: [
            {
                data: [used, remaining],
                backgroundColor: ['#00796B', '#00FFF0'],
            },
        ],
    };

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <Doughnut data={data} />
        </div>
    );
};

export default LeaveChart;
