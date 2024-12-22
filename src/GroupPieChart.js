import React, { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';

/**
 * Custom Pie chart rendered on Expense page for data visualization and quick summary
 * @param data
 * @param temp_users
 * @returns {Element}
 * @constructor
 */
const GroupPieChart = ({data,temp_users}) => {
    const chartRef = useRef(null);
    useEffect(() => {
        const merged=data.reduce((re,ob1)=>{
            const ob2= temp_users.find((ob)=>ob1.userId===ob.value);
            if (ob2){
                re.push({...ob1,...ob2})
            }
            return re;
        },[])
        console.log(merged);
        const ctx = chartRef.current.getContext('2d');
        const myChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: merged.map(e=>e.label),
                datasets: [
                    {
                        label: 'Group Expense',
                        data: merged.map(e=>e.amount),
                    },
                ],
            },
            options: {
                plugins: {
                    legend: {
                        labels: {
                            color: 'white',
                            font: {
                                size: 14,
                                family: 'Arial',
                            },
                        },
                    },
                },
            },
        });

        return () => {
            myChart.destroy();
        };
    }, [data]);

    return <canvas ref={chartRef} style={{ width: '200px', height: '100px' }}></canvas>;
};

export default GroupPieChart;
