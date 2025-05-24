import {Doughnut} from "react-chartjs-2";

type LeaveChartProps = {
    used: number;
    remaining: number;
};

const LeaveChart = ({ used, remaining }: LeaveChartProps) => {
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
            <Doughnut data={data} />
        </div>
    );
};

export default LeaveChart;
