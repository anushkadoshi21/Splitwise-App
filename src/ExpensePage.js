import React, { useEffect, useState} from 'react';
import axios from 'axios';
import './ExpensePage.css';
import './SettleUp.css';
import {useLocation} from "react-router-dom";
import GroupPieChart from './GroupPieChart';
import Select from "react-select";

const ExpensePage = () => {
    const [expenses, setExpenses] = useState([]);
    const[users,setUsers]=useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [refresh, setRefresh] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [expenseMembers, setExpenseMembers] = useState('');
    const [expenseName, setExpenseName]=useState('');
    const [memberPaidId, setMemberPaidId]=useState('');
    const [totalExpense,setTotalExpense]=useState('');
    const [oweList,setOweList]=useState([]);
    const [successPopup, setSuccessPopup] = useState(false);
    const [createExpenseSuccess,setCreateExpenseSuccess]=useState(false);
    const [userOwe,setUserOwe]=useState(false);
    const location = useLocation();
    const {groupName,groupId,userId}=location.state || {};
    const [summaryData, setSummaryData] = useState([]);

    /**
     * Handles settlement
     * @param e
     * @returns {Promise<void>}
     */
    const handleSettlement = async(e) =>{
        e.preventDefault();
        try{
            const response= await axios.post(`http://localhost:8080/expenses/settleExpense/${groupId}/${userId}`)
            if (response.status === 200) {
                setSuccessPopup(true); // Show success popup
                setTimeout(() => setSuccessPopup(false), 5000); // Hide after 5 seconds
                setRefresh((prev) => !prev);
            }
        }
        catch (err) {
            setError(err.response?.data?.message || 'Failed to settle up');
        }

    }


    /**
     * Create Expense
     * @param e
     * @returns {Promise<void>}
     */
    const handleCreateExpense = async (e) => {
        e.preventDefault();
        try {
            let members = expenseMembers.map((member) => member.value);
            console.log(members)
            const response = await axios.post(`http://localhost:8080/expenses/createExpense`, {
                expenseName : expenseName,
                membersParticipated: members,
                groupId: groupId,
                memberPaidId: memberPaidId.value,
                totalExpense: totalExpense
            });
            console.log(response)
            if (response.status===200){
                setCreateExpenseSuccess(true);
                setTimeout(() => setCreateExpenseSuccess(false), 5000);
            }
            setIsModalOpen(false);
            setExpenseName('');
            setExpenseMembers('');
            setMemberPaidId('');
            setTotalExpense('');
            setRefresh((prev) => !prev);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create expense');
        }
    };

    /**
     * Refreshes the page and all states whenever state of refresh or groupId is changed
     */
    useEffect(() => {
        var temp_users;
        const fetchExpenses = async () => {
            try {
                let grps= await axios.get(`http://localhost:8080/groups/getGroup/${groupId}`)
                let mem= await axios.get(`http://localhost:8080/users/allUsers`);
                console.log(grps)
                console.log(mem)
                let options = mem.data.filter((grp) => grps.data.groupMembers.includes(grp.userId));
                options=options.map((user) => ({
                    value: user.userId,
                    label: user.userName,
                }));
                setUsers(options);
                temp_users=options;
                const response = await axios.get(`http://localhost:8080/expenses/getGroupExpenses/${groupId}`);
                setExpenses(response.data);
                const owe= await axios.get(`http://localhost:8080/expenses/getOweLists/${groupId}`);
                setOweList(owe.data);
                setUserOwe(owe.data.some(item => item.oweId.toString()===userId.toString()))
            } catch (err) {
                setError(err.response?.data?.message || 'Failed to fetch expenses');
            } finally {
                setLoading(false);
            }
        };

        const fetchGroupSummary = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/expenses/getGroupSummary/${groupId}`);
                console.log(response.data)
                console.log(temp_users)
                setSummaryData(response.data);
            } catch (err) {
                setError(err.response?.data?.message || 'Failed to fetch group summary');
            }
        };

        fetchExpenses();
        fetchGroupSummary();
    }, [groupId,refresh]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;


    const settledExpenses = expenses.filter((expense) => expense.settleStatus);
    const unsettledExpenses = expenses.filter((expense) => !expense.settleStatus);


    /**
     * Logic for rendering different components based on states of different attributes
     */
    return (
        <div className="expenses-container">
            <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
            <button onClick={() => setIsModalOpen(true)} style={{marginBottom: '20px'}}>
                Create Expense
            </button>
            <button onClick={handleSettlement} disabled ={!userOwe} style={{marginBottom: '20px'}}>
                Settle Up!
            </button>
            </div>
            {successPopup && (
                <div className="popup">
                    <p>Success! Expense settled.</p>
                </div>
            )}
            {createExpenseSuccess && (
                <div className="popup">
                    <p>Success! Expense Created.</p>
                </div>
            )}
            {error && <p className="error">{error}</p>}
            {isModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h2 style={{color:"black"}}>Create New Expense</h2>
                        <form onSubmit={handleCreateExpense}>
                            <label>
                                Expense Name:
                                <input
                                    type="text"
                                    value={expenseName}
                                    onChange={(e) => setExpenseName(e.target.value)}
                                    required
                                />
                            </label>
                            <br/>
                            <label>
                                Total Expense:
                                <input
                                    type="text"
                                    value={totalExpense}
                                    onChange={(e) => setTotalExpense(e.target.value)}
                                    required
                                />
                            </label>
                            <br/>
                            <label>
                                Member Paid:
                                <Select
                                    options={users}
                                    value={memberPaidId}
                                    onChange={(selected) => setMemberPaidId(selected)}
                                />
                            </label>
                            <label>
                                Members Participated:
                                <Select
                                    isMulti
                                    options={users}
                                    value={expenseMembers}
                                    onChange={(selected) => setExpenseMembers(selected)}
                                />
                            </label>
                            <br/>
                            <button type="submit">Create</button>
                            <button type="button" onClick={() => setIsModalOpen(false)}>
                                Cancel
                            </button>
                        </form>
                    </div>
                </div>
            )}
            {expenses.length === 0 ? (
                <p>No expenses added yet.</p>
            ) : (
                <div className="timelines-container">
                    <div className="timeline">
                        <h2>Unsettled Expenses</h2>
                        {unsettledExpenses.length === 0 ? (
                            <p>All expenses are settled!</p>
                        ) : (
                            unsettledExpenses.map((expense, index) => (
                                <div key={expense.id} className="timeline-item">
                                    <div className="status-badge not-settled">Not Settled</div>
                                    <div className="timeline-content">
                                        <h3>{expense.expenseName}</h3>
                                        <p><strong>Total Expense:</strong> ${expense.totalExpense.toFixed(2)}</p>
                                        <p><strong>Paid
                                            by:</strong> {users.filter((e) => expense.memberPaidId === e.value).map(e => e.label)}
                                        </p>
                                        <p>
                                            <strong>Participants:</strong>{' '}
                                            {JSON.parse(expense.membersParticipated).length !== 0
                                                // ? JSON.parse(expense.membersParticipated).join(', ')
                                                ? users.filter((e) => expense.membersParticipated.includes(e.value)).map(e => e.label).join(', ')
                                                : 'No participants'}
                                        </p>
                                        <p>
                                            <strong>Payment Pending By:</strong>{' '}
                                            {/*{JSON.parse(expense.membersUnPaid).join(', ')}*/}
                                            {users.filter((e) => expense.membersUnPaid.includes(e.value)).map(e => e.label).join(', ')}
                                        </p>
                                    </div>
                                    {index < unsettledExpenses.length - 1 && <div className="timeline-line"></div>}
                                </div>
                            ))
                        )}
                    </div>
                    <div className="timeline">
                        <h1>Expenses for {groupName}</h1>

                        {<div className= "user-summary">
                            {oweList.length === 0 && <p> All Settled up for you!</p> }
                            <div>
                                {oweList.map((owe) => (
                                    <div>
                                        <p>{users.find(op=>op.value===owe.oweId).label} owes {users.find(op=>op.value===owe.owedId).label} ${owe.totalOwed.toFixed(2)}</p>

                                    </div>
                                ))}
                            </div>
                        </div>

                        }
                        {
                            <div className="pie-chart-container">
                                <h2>Group Spending Breakdown</h2>
                                <GroupPieChart  data={summaryData} temp_users={users}/>
                            </div>
                        }
                    </div>
                    <div className="timeline">
                        <h2>Settled Expenses</h2>
                        {settledExpenses.length === 0 ? (
                            <p>No settled expenses yet.</p>
                        ) : (
                            settledExpenses.map((expense, index) => (
                                <div key={expense.id} className="timeline-item">
                                    <div className="status-badge settled">Settled</div>
                                    <div className="timeline-content">
                                        <h3>{expense.expenseName}</h3>
                                        <p><strong>Total Expense:</strong> ${expense.totalExpense.toFixed(2)}</p>
                                        <p><strong>Paid
                                            by:</strong> {users.filter((e) => expense.memberPaidId === e.value).map(e => e.label)}
                                        </p>
                                        <p>
                                            <strong>Participants:</strong>{' '}
                                            {JSON.parse(expense.membersParticipated).length !== 0
                                                // ? JSON.parse(expense.membersParticipated).join(', ')
                                                ? users.filter((e) => expense.membersParticipated.includes(e.value)).map(e => e.label).join(', ')
                                                : 'No participants'}
                                        </p>
                                    </div>
                                    {index < settledExpenses.length - 1 && <div className="timeline-line"></div>}
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )
            }
        </div>);
}

export default ExpensePage;
