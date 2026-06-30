import React, { useState } from 'react';
import type { UserRole, Permission } from '@/types';
import { Checkbox } from '@/components/ui/checkbox'; // Assuming a generic Checkbox component exists
import { useHasPermission } from '@/hooks/use-has-permission';

interface UserRowProps {
  user: {
    id: string;
    nom: string;
    prenom: string;
    email: string;
    role: UserRole;
    permissions: Permission[];
  };
  onRoleChange: (role: UserRole) => void;
  onPermissionsChange: (permissions: string[]) => void;
}

export const UserRow: React.FC<UserRowProps> = ({ user, onRoleChange, onPermissionsChange }) => {
  const [selectedRole, setSelectedRole] = useState<UserRole>(user.role);
  const [selectedPermissions, setSelectedPermissions] = useState<string[]>(
    user.permissions.map(p => p.id)
  );

  const roleOptions: UserRole[] = [
    'ADMIN',
    'DOCTOR',
    'NURSE',
    'PATIENT',
    'HR',
    'PHARMACIST',
    'RADIOLOGIST',
    'RECEPTIONIST',
    'ACCOUNTANT',
    'LABORATORY_TECHNICIAN',
  ];

  const handleRoleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newRole = e.target.value as UserRole;
    setSelectedRole(newRole);
    onRoleChange(newRole);
  };

  const togglePermission = (permId: string) => {
    const updated = selectedPermissions.includes(permId)
      ? selectedPermissions.filter(id => id !== permId)
      : [...selectedPermissions, permId];
    setSelectedPermissions(updated);
    onPermissionsChange(updated);
  };

  return (
    <tr className="hover:bg-muted/30">
      <td className="px-4 py-2">{user.prenom} {user.nom}</td>
      <td className="px-4 py-2">{user.email}</td>
      <td className="px-4 py-2">
        <select
          value={selectedRole}
          onChange={handleRoleChange}
          className="rounded border p-1 text-sm"
        >
          {roleOptions.map(r => (
            <option key={r} value={r}> {r} </option>
          ))}
        </select>
      </td>
      <td className="px-4 py-2 space-x-1">
        {/* Simple permission list – in a real app this could be a modal */}
        {user.permissions.map(p => (
          <label key={p.id} className="inline-flex items-center space-x-1">
            <Checkbox
              checked={selectedPermissions.includes(p.id)}
              onCheckedChange={() => togglePermission(p.id)}
            />
            <span className="text-xs">{p.name}</span>
          </label>
        ))}
      </td>
       <td className="px-4 py-2">
         {/* Placeholder for additional actions such */}
         {useHasPermission('users:UPDATE') && (
           <button
             onClick={() => console.log('Save action can be triggered here')}
             className="px-2 py-1 bg-primary text-primary-foreground rounded hover:bg-primary/90 transition"
           >
             Save
           </button>
         )}
       </td>
    </tr>
  );
};
