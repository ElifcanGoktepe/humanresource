import { Doughnut } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend
} from 'chart.js';
ChartJS.register(ArcElement, Tooltip, Legend);

const LeaveChart = () => {
    const data = {
        labels: ['Kullanılan', 'Kalan'],
        datasets: [{
            data: [12, 8], // örnek veri
            backgroundColor: ['#00796B', '#00ffe1']
        }]
    };

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <Doughnut data={data} />
        </div>
    );
};

export default LeaveChart;
