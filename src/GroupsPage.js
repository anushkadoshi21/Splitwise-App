import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {useLocation} from 'react-router-dom';
import './GroupsPage.css';
import { useNavigate } from 'react-router-dom';
import Select from 'react-select';

const GroupsPage = () => {
    const [summary,setSummary]= useState({ totalOwed: 0, totalGets: 0 });
    const[loadSummary,setLoadSummary]=useState(true);
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const location = useLocation();
    const {userId,userName}=location.state || {};
    // const { userId } = useParams();
    const [isModalOpen, setIsModalOpen] = useState(false); // Modal state
    const [groupName, setGroupName] = useState('');
    const [groupMembers, setGroupMembers] = useState([]);
    const [refresh, setRefresh] = useState(false);
    const [users,setUsers]=useState([]);
    const navigate = useNavigate();
    const handleCreateGroup = async (e) => {
        e.preventDefault();
        try {
            let members = groupMembers.map((member) => member.value);
            members.push(userId);
            console.log(members)
            const response = await axios.post(`http://localhost:8080/groups/createGroup`, {
                groupName : groupName,
                groupMembers: members,
            });

            setIsModalOpen(false);
            setGroupName('');
            setGroupMembers('');
            setRefresh((prev) => !prev);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create group');
        }
    };

    /**
     * Refreshes the page whenver a state change is detected in userId or refresh
     */
    useEffect(() => {
        const fetchSummary = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8080/users/${userId}/summary`,{headers: {
                        'Accept': 'application/json'
                }}
                );
                setSummary(response.data);
            } catch (err) {
                setError(err.response?.data?.message || "Failed to fetch summary");
            } finally {
                setLoadSummary(false);
            }
        };

        const fetchGroups = async () => {
            try {
                let users= await axios.get(`http://localhost:8080/users/allUsers`)
                users=users.data.filter(u => u.userId!==userId);
                const options = users.map((user) => ({
                    value: user.userId,
                    label: user.userName,
                }));
                setUsers(options);
                const response = await axios.get(`http://localhost:8080/groups/userGroups/${userId}`, {
                    params: { userId },
                });
                setGroups(response.data);
            } catch (err) {
                setError(err.response?.data?.message || 'Failed to fetch groups');
            } finally {
                setLoading(false);
            }
        };

        fetchGroups();
        fetchSummary();
    }, [userId,refresh]);

    /**
     * Navigates user to specific group when clicked on group card
     * @param groupId
     * @param name
     */
    const handleGroupClick = (groupId,name) => {
        navigate(`/loggedInUser/expensePage`,{state: {
                groupName: name,
                groupId: groupId,
                userId: userId
        }});
    };

    if (loading) return <p>Loading groups...</p>;
    if (error) return <p>Error: {error}</p>;


    /**
     * Logic for rendering different components based on states of different attributes
     */
    return (
        <div className="groups-container">
            <h1>Welcome, {userName}!</h1>
            {/*<div className="user-summary">*/}
                <p className="text owes"><strong>You owe:</strong> ${summary.totalOwed.toFixed(2)}</p>
                <br />
                <p className="text gets"><strong>You get:</strong> ${summary.totalGets.toFixed(2)}</p>
            {/*</div>*/}
            <h2>Your Groups</h2>
            <button onClick={() => setIsModalOpen(true)} style={{marginBottom: '20px'}}>
                Create Group
            </button>
            {isModalOpen && (
                <div className="modal">
                    <div className="modal-content">
                        <h2 style={{color:"black"}}>Create New Group</h2>
                        <form onSubmit={handleCreateGroup}>
                            <label>
                                Group Name:
                                <input
                                    type="text"
                                    value={groupName}
                                    onChange={(e) => setGroupName(e.target.value)}
                                    required
                                />
                            </label>
                            <br/>
                            <label>
                                Add Members:
                                <Select
                                    isMulti
                                    options={users}
                                    value={groupMembers}
                                    onChange={(selected) => setGroupMembers(selected)}
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

            <div className="group-cards">
                {groups.length > 0 ? (
                    groups.map((group) => (
                        <div key={group.groupId} className="group-card"
                             onClick={() => handleGroupClick(group.groupId, group.groupName)}>
                            <h2>{group.groupName}</h2>
                            <p>Members: {group.memberCount}</p>
                        </div>
                    ))
                ) : (
                    <p>You are not part of any groups yet.</p>
                )}
            </div>
        </div>
    );
};

export default GroupsPage;