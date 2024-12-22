import './App.css';
import LoginPage from "./LoginPage";
import GroupsPage from './GroupsPage';
import ExpensePage from './ExpensePage';
import {BrowserRouter, Route, Routes} from 'react-router-dom';

/**
 * Entry point of Frontend which describes routes
 * @returns {JSX.Element}
 * @constructor
 */
function App() {
    return (
        <div id="main-container">
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/loggedInUser/expensePage" element={<ExpensePage/>}/>
                    <Route path="/loggedInUser/groupPage" element={<GroupsPage/>}/>

                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
