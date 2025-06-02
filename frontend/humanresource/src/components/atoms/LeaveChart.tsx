import { useEffect, useRef, useState } from 'react';
import { Doughnut } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    ArcElement,
    Tooltip,
    Legend
} from 'chart.js';
import axios from 'axios';

ChartJS.register(ArcElement, Tooltip, Legend);

type LeaveChartProps = {
    onDataReady?: (data: { total: number; used: number; remaining: number }) => void;
};

const LeaveChart = ({ onDataReady }: LeaveChartProps) => {
    const [used, setUsed] = useState(0);
    const [remaining, setRemaining] = useState(0);
    const [total, setTotal] = useState(0);
    const hasSentData = useRef(false);

    useEffect(() => {
        const fetchLeaveData = async () => {
            const token = localStorage.getItem("token");
            try {
                const response = await axios.get("http://localhost:9090/employee/leave-usage", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                const { used, total, remaining } = response.data.data;
                setUsed(used);
                setTotal(total);
                setRemaining(remaining);

                if (onDataReady && !hasSentData.current) {
                    onDataReady({ total, used, remaining });
                    hasSentData.current = true;
                }
            } catch (error) {
                console.error("Leave data fetch error", error);
            }
        };

        fetchLeaveData();
    }, [onDataReady]);

    const data = {
        labels: ['Used', 'Remaining'],
        datasets: [
            {
                data: [used, remaining],
                backgroundColor: ['#00796B', '#00FFF0'],
            },
        ],
    };

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <div style={{ textAlign: 'center', marginBottom: '10px' }}>
                <strong>Toplam İzin Hakkı:</strong> {total} gün
            </div>
            <Doughnut data={data} />
        </div>
    );
};

export default LeaveChart;
